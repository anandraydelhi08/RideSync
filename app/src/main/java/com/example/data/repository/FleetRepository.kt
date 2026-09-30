package com.example.data.repository

import com.example.data.demo.DemoDataGenerator
import com.example.data.local.dao.FleetDao
import com.example.data.local.entity.*
import com.example.data.model.DutyStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class FleetRepository(private val fleetDao: FleetDao) {

    val allBookings: Flow<List<BookingEntity>> = fleetDao.getAllBookings()
    val allDrivers: Flow<List<DriverEntity>> = fleetDao.getAllDrivers()
    val allVehicles: Flow<List<VehicleEntity>> = fleetDao.getAllVehicles()
    val allCustomers: Flow<List<CustomerEntity>> = fleetDao.getAllCustomers()
    val allSuppliers: Flow<List<SupplierEntity>> = fleetDao.getAllSuppliers()
    val allInvoices: Flow<List<InvoiceEntity>> = fleetDao.getAllInvoices()
    val allExpenses: Flow<List<ExpenseEntity>> = fleetDao.getAllExpenses()
    val allAlerts: Flow<List<AlertEntity>> = fleetDao.getAllAlerts()

    suspend fun initializeDemoDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingBookings = fleetDao.getAllBookings().first()
        if (existingBookings.isEmpty()) {
            fleetDao.insertAllDrivers(DemoDataGenerator.generateDrivers())
            fleetDao.insertAllVehicles(DemoDataGenerator.generateVehicles())
            fleetDao.insertAllCustomers(DemoDataGenerator.generateCustomers())
            fleetDao.insertAllSuppliers(DemoDataGenerator.generateSuppliers())
            fleetDao.insertAllBookings(DemoDataGenerator.generateBookings())
            fleetDao.insertAllInvoices(DemoDataGenerator.generateInvoices())
            fleetDao.insertAllExpenses(DemoDataGenerator.generateExpenses())
            fleetDao.insertAllAlerts(DemoDataGenerator.generateAlerts())
            fleetDao.insertAllLogs(DemoDataGenerator.generateActivityLogs())
        }
    }

    fun getBookingFlow(id: String): Flow<BookingEntity?> = fleetDao.getBookingFlowById(id)

    fun getLogsForBooking(bookingId: String): Flow<List<ActivityLogEntity>> = fleetDao.getLogsForBooking(bookingId)

    suspend fun getBookingById(id: String): BookingEntity? = withContext(Dispatchers.IO) {
        fleetDao.getBookingById(id)
    }

    suspend fun createBooking(booking: BookingEntity) = withContext(Dispatchers.IO) {
        fleetDao.insertBooking(booking)
        fleetDao.insertLog(
            ActivityLogEntity(
                bookingId = booking.id,
                action = "BOOKING_CREATED",
                oldState = null,
                newState = booking.status,
                performedBy = "Operations Console",
                notes = "New booking created for ${booking.customerName}"
            )
        )
    }

    suspend fun updateBooking(booking: BookingEntity) = withContext(Dispatchers.IO) {
        fleetDao.updateBooking(booking)
    }

    suspend fun assignDriverAndVehicle(
        bookingId: String,
        driverId: String,
        vehicleId: String,
        supplierId: String? = null
    ) = withContext(Dispatchers.IO) {
        val booking = fleetDao.getBookingById(bookingId) ?: return@withContext
        val driver = fleetDao.getDriverById(driverId)
        val vehicle = fleetDao.getVehicleById(vehicleId)
        val supplier = supplierId?.let { fleetDao.getSupplierById(it) }

        val updated = booking.copy(
            assignedDriverId = driverId,
            assignedDriverName = driver?.name,
            assignedDriverPhone = driver?.mobile,
            assignedVehicleId = vehicleId,
            assignedVehicleReg = vehicle?.registrationNumber,
            assignedVehicleModel = "${vehicle?.make} ${vehicle?.model}",
            assignedSupplierId = supplierId,
            assignedSupplierName = supplier?.companyName,
            status = DutyStatus.ALLOTTED.name,
            updatedAt = System.currentTimeMillis()
        )
        fleetDao.updateBooking(updated)

        driver?.let {
            fleetDao.updateDriver(it.copy(status = "Assigned", assignedVehicleReg = vehicle?.registrationNumber ?: it.assignedVehicleReg))
        }
        vehicle?.let {
            fleetDao.updateVehicle(it.copy(status = "Assigned"))
        }

        fleetDao.insertLog(
            ActivityLogEntity(
                bookingId = bookingId,
                action = "DRIVER_ALLOTTED",
                oldState = booking.status,
                newState = DutyStatus.ALLOTTED.name,
                performedBy = "Dispatcher",
                notes = "Assigned ${driver?.name ?: driverId} with vehicle ${vehicle?.registrationNumber ?: vehicleId}"
            )
        )
    }

    suspend fun transitionDutyStatus(
        bookingId: String,
        newStatus: DutyStatus,
        performedBy: String = "Dispatcher",
        notes: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        val booking = fleetDao.getBookingById(bookingId) ?: return@withContext false
        val currentStatus = try { DutyStatus.valueOf(booking.status) } catch (e: Exception) { DutyStatus.BOOKED }

        // Update booking state
        val updated = booking.copy(
            status = newStatus.name,
            updatedAt = System.currentTimeMillis()
        )
        fleetDao.updateBooking(updated)

        // Log transition
        fleetDao.insertLog(
            ActivityLogEntity(
                bookingId = bookingId,
                action = "STATUS_CHANGE",
                oldState = currentStatus.name,
                newState = newStatus.name,
                performedBy = performedBy,
                notes = notes ?: "Transitioned from ${currentStatus.label} to ${newStatus.label}"
            )
        )

        // Update driver & vehicle statuses if applicable
        if (newStatus == DutyStatus.STARTED || newStatus == DutyStatus.IN_PROGRESS || newStatus == DutyStatus.ENROUTE) {
            booking.assignedDriverId?.let { drvId ->
                val drv = fleetDao.getDriverById(drvId)
                drv?.let { fleetDao.updateDriver(it.copy(status = "On Duty")) }
            }
            booking.assignedVehicleId?.let { vehId ->
                val veh = fleetDao.getVehicleById(vehId)
                veh?.let { fleetDao.updateVehicle(it.copy(status = "On Duty")) }
            }
        } else if (newStatus == DutyStatus.CLOSED || newStatus == DutyStatus.CANCELLED) {
            booking.assignedDriverId?.let { drvId ->
                val drv = fleetDao.getDriverById(drvId)
                drv?.let { fleetDao.updateDriver(it.copy(status = "Available")) }
            }
            booking.assignedVehicleId?.let { vehId ->
                val veh = fleetDao.getVehicleById(vehId)
                veh?.let { fleetDao.updateVehicle(it.copy(status = "Available")) }
            }
        }

        true
    }

    suspend fun startDutyWithOtp(
        bookingId: String,
        enteredOtp: String,
        startKm: Double
    ): Result<BookingEntity> = withContext(Dispatchers.IO) {
        val booking = fleetDao.getBookingById(bookingId) ?: return@withContext Result.failure(Exception("Booking not found"))

        if (booking.passengerOtp != enteredOtp.trim()) {
            return@withContext Result.failure(Exception("Invalid OTP! Check passenger OTP."))
        }

        val updated = booking.copy(
            status = DutyStatus.STARTED.name,
            startKm = startKm,
            startTime = System.currentTimeMillis(),
            isOtpVerified = true,
            updatedAt = System.currentTimeMillis()
        )
        fleetDao.updateBooking(updated)

        fleetDao.insertLog(
            ActivityLogEntity(
                bookingId = bookingId,
                action = "DUTY_STARTED",
                oldState = booking.status,
                newState = DutyStatus.STARTED.name,
                performedBy = "Chauffeur App",
                notes = "OTP Verified ($enteredOtp). Start Odo: $startKm KM"
            )
        )

        Result.success(updated)
    }

    suspend fun completeDuty(
        bookingId: String,
        endKm: Double,
        toll: Double,
        parking: Double,
        rating: Float,
        feedback: String?
    ): Result<BookingEntity> = withContext(Dispatchers.IO) {
        val booking = fleetDao.getBookingById(bookingId) ?: return@withContext Result.failure(Exception("Booking not found"))

        val totalKmRun = (endKm - booking.startKm).coerceAtLeast(0.0)
        val extraKm = (totalKmRun - 40.0).coerceAtLeast(0.0) // default package allowance
        val extraKmCharge = extraKm * booking.extraKmRate
        val totalCustomer = booking.baseFare + extraKmCharge + toll + parking + booking.driverAllowance
        val gst = totalCustomer * 0.05
        val finalAmount = totalCustomer + gst
        val dutyProfit = (finalAmount - booking.supplierCost - toll - parking).coerceAtLeast(0.0)

        val updated = booking.copy(
            status = DutyStatus.COMPLETED.name,
            endKm = endKm,
            endTime = System.currentTimeMillis(),
            tollCharges = toll,
            parkingCharges = parking,
            passengerRating = rating,
            passengerFeedback = feedback,
            gstAmount = gst,
            totalCustomerAmount = finalAmount,
            dutyProfit = dutyProfit,
            updatedAt = System.currentTimeMillis()
        )
        fleetDao.updateBooking(updated)

        fleetDao.insertLog(
            ActivityLogEntity(
                bookingId = bookingId,
                action = "DUTY_COMPLETED",
                oldState = booking.status,
                newState = DutyStatus.COMPLETED.name,
                performedBy = "Chauffeur App",
                notes = "End Odo: $endKm KM. Total Run: $totalKmRun KM. Fare: ₹$finalAmount"
            )
        )

        Result.success(updated)
    }

    suspend fun generateInvoiceForBooking(bookingId: String): InvoiceEntity = withContext(Dispatchers.IO) {
        val booking = fleetDao.getBookingById(bookingId) ?: throw Exception("Booking not found")
        val invId = "INV-2026-00" + (100..999).random()
        val customer = fleetDao.getCustomerById(booking.customerId)
        val taxable = booking.totalCustomerAmount - booking.gstAmount
        val cgst = booking.gstAmount / 2.0
        val sgst = booking.gstAmount / 2.0

        val invoice = InvoiceEntity(
            id = invId,
            bookingId = bookingId,
            customerId = booking.customerId,
            customerName = booking.customerName,
            gstin = customer?.gstin ?: "29AAACI1234F1Z8",
            invoiceDate = System.currentTimeMillis(),
            dueDate = System.currentTimeMillis() + (30L * 24 * 3600 * 1000),
            taxableAmount = taxable,
            cgst = cgst,
            sgst = sgst,
            totalAmount = booking.totalCustomerAmount,
            balanceAmount = booking.totalCustomerAmount,
            paymentStatus = "UNPAID"
        )
        fleetDao.insertInvoice(invoice)

        val updatedBooking = booking.copy(
            invoiceStatus = "GENERATED",
            invoiceId = invId,
            status = DutyStatus.CLOSED.name,
            updatedAt = System.currentTimeMillis()
        )
        fleetDao.updateBooking(updatedBooking)

        fleetDao.insertLog(
            ActivityLogEntity(
                bookingId = bookingId,
                action = "INVOICE_GENERATED",
                oldState = booking.status,
                newState = DutyStatus.CLOSED.name,
                performedBy = "Accounts Dept",
                notes = "Generated Invoice $invId for ₹${booking.totalCustomerAmount}"
            )
        )

        invoice
    }

    suspend fun resolveAlert(alertId: String) = withContext(Dispatchers.IO) {
        fleetDao.resolveAlert(alertId)
    }

    suspend fun addExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) {
        fleetDao.insertExpense(expense)
    }

    suspend fun updateDriverTelemetry(driverId: String, lat: Double, lng: Double, speed: Float, battery: Int) = withContext(Dispatchers.IO) {
        fleetDao.updateDriverTelemetry(driverId, lat, lng, speed, battery, System.currentTimeMillis())
    }
}
