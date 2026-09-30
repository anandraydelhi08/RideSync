package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val customerName: String,
    val bookerName: String,
    val bookerPhone: String,
    val bookerEmail: String,
    val passengerName: String,
    val passengerPhone: String,
    val passengerEmail: String,
    val passengerCount: Int = 1,
    val tripType: String,
    val vehicleCategory: String,
    val pickupCity: String,
    val pickupDateTime: Long,
    val pickupAddress: String,
    val pickupLat: Double = 12.9716,
    val pickupLng: Double = 77.5946,
    val dropCity: String,
    val dropAddress: String,
    val dropLat: Double = 13.1986,
    val dropLng: Double = 77.7066,
    val flightOrTrainNumber: String = "",
    val airportTerminal: String = "",
    val customerRemarks: String = "",
    val driverRemarks: String = "",
    val internalNotes: String = "",
    val assignedDriverId: String? = null,
    val assignedDriverName: String? = null,
    val assignedDriverPhone: String? = null,
    val assignedVehicleId: String? = null,
    val assignedVehicleReg: String? = null,
    val assignedVehicleModel: String? = null,
    val assignedSupplierId: String? = null,
    val assignedSupplierName: String? = null,
    val status: String = "BOOKED",
    val startKm: Double = 0.0,
    val endKm: Double = 0.0,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val passengerOtp: String = "4829",
    val isOtpVerified: Boolean = false,
    val passengerSignature: String? = null,
    val passengerRating: Float = 0.0f,
    val passengerFeedback: String? = null,
    val baseFare: Double = 2200.0,
    val extraKmRate: Double = 18.0,
    val extraHourRate: Double = 150.0,
    val driverAllowance: Double = 300.0,
    val tollCharges: Double = 0.0,
    val parkingCharges: Double = 0.0,
    val stateTax: Double = 0.0,
    val gstAmount: Double = 110.0,
    val discount: Double = 0.0,
    val totalCustomerAmount: Double = 2310.0,
    val supplierCost: Double = 1750.0,
    val dutyProfit: Double = 560.0,
    val paymentStatus: String = "PENDING",
    val invoiceStatus: String = "UNBILLED",
    val invoiceId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey val id: String,
    val name: String,
    val mobile: String,
    val email: String,
    val photoUrl: String = "",
    val licenseNumber: String,
    val licenseExpiryDate: String,
    val address: String,
    val emergencyContact: String,
    val joiningDate: String,
    val driverType: String = "Company",
    val salaryOrBatta: Double = 22000.0,
    val assignedBranch: String = "Bangalore Central",
    val assignedVehicleReg: String = "",
    val status: String = "Available", // Available, Assigned, On Duty, Offline
    val currentLat: Double = 12.9716,
    val currentLng: Double = 77.5946,
    val currentSpeed: Float = 0.0f,
    val batteryPct: Int = 92,
    val lastTelemetryTime: Long = System.currentTimeMillis(),
    val rating: Float = 4.8f,
    val totalTrips: Int = 342,
    val isDocExpired: Boolean = false
)

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val id: String,
    val registrationNumber: String,
    val vehicleGroup: String, // Sedan, SUV, etc.
    val make: String,
    val model: String,
    val year: Int = 2024,
    val color: String = "Pearl White",
    val fuelType: String = "Diesel",
    val seatingCapacity: Int = 4,
    val ownerType: String = "Company Owned",
    val branch: String = "Bangalore Central",
    val insuranceExpiry: String = "2027-04-15",
    val fitnessExpiry: String = "2027-08-20",
    val permitExpiry: String = "2027-11-01",
    val pucExpiry: String = "2026-12-10",
    val currentOdometer: Double = 48210.0,
    val status: String = "Available", // Available, Assigned, On Duty, Maintenance
    val fuelExpenseTotal: Double = 14200.0,
    val maintenanceExpenseTotal: Double = 6800.0,
    val isInspectionOverdue: Boolean = false
)

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: String,
    val companyName: String,
    val contactPerson: String,
    val mobile: String,
    val email: String,
    val gstin: String,
    val billingAddress: String,
    val city: String,
    val paymentTerms: String = "Net 30",
    val creditLimit: Double = 250000.0,
    val outstandingAmount: Double = 42300.0,
    val rateCardId: String = "RC-CORP-STD",
    val activeBookingsCount: Int = 3
)

@Entity(tableName = "suppliers")
data class SupplierEntity(
    @PrimaryKey val id: String,
    val companyName: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
    val gstin: String,
    val city: String,
    val bankAccount: String,
    val ifscCode: String,
    val totalPayable: Double = 31500.0,
    val activeFleetCount: Int = 12,
    val status: String = "Active"
)

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey val id: String,
    val bookingId: String,
    val customerId: String,
    val customerName: String,
    val gstin: String,
    val invoiceDate: Long = System.currentTimeMillis(),
    val dueDate: Long = System.currentTimeMillis() + (30L * 24 * 3600 * 1000),
    val taxableAmount: Double,
    val cgst: Double,
    val sgst: Double,
    val igst: Double = 0.0,
    val totalAmount: Double,
    val paidAmount: Double = 0.0,
    val balanceAmount: Double,
    val paymentStatus: String = "UNPAID", // UNPAID, PARTIAL, PAID
    val paymentMethod: String? = null
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val bookingId: String? = null,
    val driverId: String? = null,
    val vehicleReg: String? = null,
    val category: String, // Toll, Parking, Fuel, Food, Repair
    val amount: Double,
    val receiptNote: String = "",
    val isApproved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val severity: String, // Critical, Warning, Info
    val bookingId: String? = null,
    val driverId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookingId: String,
    val action: String,
    val oldState: String? = null,
    val newState: String? = null,
    val performedBy: String = "System",
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String? = null
)
