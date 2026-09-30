package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.*
import com.example.data.model.DutyStatus
import com.example.data.model.UserRole
import com.example.data.repository.FleetRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class ScreenTab(val title: String) {
    DASHBOARD("Operations"),
    DISPATCH("Dispatch Board"),
    BOOKINGS("Bookings"),
    LIVE_TRACKING("Live GPS Map"),
    DRIVER_CONSOLE("Driver Duty App"),
    FLEET("Vehicles"),
    DRIVERS("Drivers"),
    BILLING("Billing & Invoices"),
    REPORTS("Reports & P&L"),
    CORPORATE("Corporate & Suppliers"),
    SETTINGS("Settings & APIs")
}

data class FleetUiState(
    val currentRole: UserRole = UserRole.OPERATIONS_MANAGER,
    val currentTab: ScreenTab = ScreenTab.DASHBOARD,
    val selectedBookingId: String? = null,
    val isCreateBookingOpen: Boolean = false,
    val isAssignDriverOpen: Boolean = false,
    val assignBookingTarget: BookingEntity? = null,
    val selectedDriverForConsoleId: String = "DRV-107", // Default driver for driver view
    val searchQuery: String = "",
    val statusFilter: String = "ALL",
    val toastMessage: String? = null,
    val isSimulationActive: Boolean = true
)

class FleetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FleetRepository
    private var telemetryJob: Job? = null

    private val _uiState = MutableStateFlow(FleetUiState())
    val uiState: StateFlow<FleetUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = FleetRepository(database.fleetDao())

        viewModelScope.launch {
            repository.initializeDemoDataIfNeeded()
            startGpsSimulation()
        }
    }

    val bookings: StateFlow<List<BookingEntity>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val drivers: StateFlow<List<DriverEntity>> = repository.allDrivers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vehicles: StateFlow<List<VehicleEntity>> = repository.allVehicles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<CustomerEntity>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val suppliers: StateFlow<List<SupplierEntity>> = repository.allSuppliers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoices: StateFlow<List<InvoiceEntity>> = repository.allInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alerts: StateFlow<List<AlertEntity>> = repository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setRole(role: UserRole) {
        _uiState.update { it.copy(currentRole = role) }
        // Auto navigate to relevant default tab for role
        when (role) {
            UserRole.DRIVER -> setTab(ScreenTab.DRIVER_CONSOLE)
            UserRole.DISPATCHER -> setTab(ScreenTab.DISPATCH)
            UserRole.ACCOUNTS_MANAGER -> setTab(ScreenTab.BILLING)
            UserRole.CORPORATE_CUSTOMER -> setTab(ScreenTab.BOOKINGS)
            else -> {}
        }
    }

    fun setTab(tab: ScreenTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun selectBooking(bookingId: String?) {
        _uiState.update { it.copy(selectedBookingId = bookingId) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { it.copy(statusFilter = status) }
    }

    fun openCreateBooking(open: Boolean) {
        _uiState.update { it.copy(isCreateBookingOpen = open) }
    }

    fun openAssignModal(booking: BookingEntity?) {
        _uiState.update { it.copy(isAssignDriverOpen = booking != null, assignBookingTarget = booking) }
    }

    fun setDriverForConsole(driverId: String) {
        _uiState.update { it.copy(selectedDriverForConsoleId = driverId) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun createNewBooking(
        customerId: String,
        customerName: String,
        bookerName: String,
        bookerPhone: String,
        bookerEmail: String,
        passengerName: String,
        passengerPhone: String,
        tripType: String,
        vehicleCategory: String,
        pickupCity: String,
        pickupAddress: String,
        dropCity: String,
        dropAddress: String,
        pickupDateTime: Long,
        flightOrTrain: String = "",
        remarks: String = "",
        baseFare: Double = 2400.0
    ) {
        viewModelScope.launch {
            val autoId = "BK-" + (83000..89999).random()
            val otp = (1000..9999).random().toString()
            val gst = baseFare * 0.05
            val total = baseFare + gst
            val supplierCost = baseFare * 0.75
            val profit = total - supplierCost

            val newBooking = BookingEntity(
                id = autoId,
                customerId = customerId,
                customerName = customerName,
                bookerName = bookerName,
                bookerPhone = bookerPhone,
                bookerEmail = bookerEmail,
                passengerName = passengerName,
                passengerPhone = passengerPhone,
                passengerEmail = bookerEmail,
                tripType = tripType,
                vehicleCategory = vehicleCategory,
                pickupCity = pickupCity,
                pickupDateTime = pickupDateTime,
                pickupAddress = pickupAddress,
                dropCity = dropCity,
                dropAddress = dropAddress,
                flightOrTrainNumber = flightOrTrain,
                customerRemarks = remarks,
                status = DutyStatus.BOOKED.name,
                passengerOtp = otp,
                baseFare = baseFare,
                gstAmount = gst,
                totalCustomerAmount = total,
                supplierCost = supplierCost,
                dutyProfit = profit
            )
            repository.createBooking(newBooking)
            _uiState.update { it.copy(isCreateBookingOpen = false, toastMessage = "Booking $autoId Created Successfully!") }
        }
    }

    fun assignDriver(bookingId: String, driverId: String, vehicleId: String, supplierId: String? = null) {
        viewModelScope.launch {
            repository.assignDriverAndVehicle(bookingId, driverId, vehicleId, supplierId)
            _uiState.update { it.copy(isAssignDriverOpen = false, assignBookingTarget = null, toastMessage = "Driver and Vehicle Allotted!") }
        }
    }

    fun transitionStatus(bookingId: String, newStatus: DutyStatus, notes: String? = null) {
        viewModelScope.launch {
            val user = _uiState.value.currentRole.displayName
            val success = repository.transitionDutyStatus(bookingId, newStatus, user, notes)
            if (success) {
                _uiState.update { it.copy(toastMessage = "Status updated to ${newStatus.label}") }
            }
        }
    }

    fun driverAcceptDuty(bookingId: String) {
        transitionStatus(bookingId, DutyStatus.DRIVER_ACCEPTED, "Driver accepted duty via mobile console")
    }

    fun driverRejectDuty(bookingId: String) {
        transitionStatus(bookingId, DutyStatus.REJECTED, "Driver declined assignment")
    }

    fun driverEnroute(bookingId: String) {
        transitionStatus(bookingId, DutyStatus.ENROUTE, "Driver heading towards pickup point")
    }

    fun driverArrived(bookingId: String) {
        transitionStatus(bookingId, DutyStatus.ARRIVED, "Driver arrived at passenger location")
    }

    fun driverStartDuty(bookingId: String, otp: String, startKm: Double) {
        viewModelScope.launch {
            val result = repository.startDutyWithOtp(bookingId, otp, startKm)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(toastMessage = "Trip Started! OTP verified.") }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(toastMessage = error.message ?: "Failed to start duty") }
                }
            )
        }
    }

    fun driverCompleteDuty(bookingId: String, endKm: Double, toll: Double, parking: Double, rating: Float, feedback: String?) {
        viewModelScope.launch {
            val result = repository.completeDuty(bookingId, endKm, toll, parking, rating, feedback)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(toastMessage = "Duty Completed! Ready for billing.") }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(toastMessage = error.message ?: "Failed to complete duty") }
                }
            )
        }
    }

    fun generateInvoice(bookingId: String) {
        viewModelScope.launch {
            try {
                val inv = repository.generateInvoiceForBooking(bookingId)
                _uiState.update { it.copy(toastMessage = "Tax Invoice ${inv.id} generated successfully!") }
            } catch (e: Exception) {
                _uiState.update { it.copy(toastMessage = "Error generating invoice: ${e.message}") }
            }
        }
    }

    fun resolveAlert(alertId: String) {
        viewModelScope.launch {
            repository.resolveAlert(alertId)
            _uiState.update { it.copy(toastMessage = "Alert resolved") }
        }
    }

    fun addExpense(driverId: String, vehicleReg: String, category: String, amount: Double, note: String, bookingId: String? = null) {
        viewModelScope.launch {
            val exp = ExpenseEntity(
                id = "EXP-" + (600..999).random(),
                bookingId = bookingId,
                driverId = driverId,
                vehicleReg = vehicleReg,
                category = category,
                amount = amount,
                receiptNote = note,
                isApproved = false
            )
            repository.addExpense(exp)
            _uiState.update { it.copy(toastMessage = "Expense recorded: ₹$amount ($category)") }
        }
    }

    // Live GPS telemetry background simulator
    private fun startGpsSimulation() {
        telemetryJob?.cancel()
        telemetryJob = viewModelScope.launch {
            var step = 0
            while (isActive) {
                delay(4000)
                if (_uiState.value.isSimulationActive) {
                    step++
                    // Simulated gentle movement for on-duty drivers along Bangalore corridors
                    val deltaLat = (kotlin.math.sin(step * 0.15) * 0.0018)
                    val deltaLng = (kotlin.math.cos(step * 0.15) * 0.0022)
                    val speed1 = (36f + (kotlin.math.sin(step.toDouble()).toFloat() * 14f)).coerceIn(10f, 65f)
                    val speed2 = (48f + (kotlin.math.cos(step.toDouble()).toFloat() * 12f)).coerceIn(20f, 75f)

                    repository.updateDriverTelemetry("DRV-107", 12.9352 + deltaLat, 77.6245 + deltaLng, speed1, 64)
                    repository.updateDriverTelemetry("DRV-102", 13.1986 - deltaLat, 77.7066 - deltaLng, speed2, 89)
                    repository.updateDriverTelemetry("DRV-104", 13.0358 + deltaLat, 77.5970 + deltaLng, 42.0f, 77)
                }
            }
        }
    }

    fun toggleSimulation() {
        _uiState.update { it.copy(isSimulationActive = !it.isSimulationActive) }
    }
}
