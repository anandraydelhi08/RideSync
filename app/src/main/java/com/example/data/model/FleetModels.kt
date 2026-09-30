package com.example.data.model

enum class UserRole(val displayName: String, val badgeColorHex: Long) {
    SUPER_ADMIN("Super Admin", 0xFF6366F1),
    COMPANY_ADMIN("Company Admin", 0xFF0284C7),
    OPERATIONS_MANAGER("Operations Manager", 0xFF0D9488),
    BOOKING_EXECUTIVE("Booking Executive", 0xFF3B82F6),
    DISPATCHER("Dispatcher", 0xFF8B5CF6),
    ACCOUNTS_MANAGER("Accounts Manager", 0xFFD97706),
    CORPORATE_CUSTOMER("Corporate Customer", 0xFF10B981),
    DRIVER("Driver", 0xFFF97316),
    SUPPLIER("Supplier / Vendor", 0xFFEC4899);

    companion object {
        fun fromString(value: String): UserRole {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: OPERATIONS_MANAGER
        }
    }
}

enum class DutyStatus(val label: String, val stepIndex: Int) {
    BOOKED("Booked", 0),
    CONFIRMED("Confirmed", 1),
    ALLOTTED("Allotted", 2),
    DRIVER_ACCEPTED("Driver Accepted", 3),
    DISPATCHED("Dispatched", 4),
    ENROUTE("Enroute", 5),
    ARRIVED("Arrived", 6),
    STARTED("Started", 7),
    IN_PROGRESS("In Progress", 8),
    COMPLETED("Completed", 9),
    CLOSED("Closed", 10),
    CANCELLED("Cancelled", -1),
    NO_SHOW("No Show", -1),
    REJECTED("Rejected", -1);

    fun canTransitionTo(next: DutyStatus): Boolean {
        if (this == CLOSED || this == CANCELLED) return false
        return when (this) {
            BOOKED -> next in listOf(CONFIRMED, CANCELLED)
            CONFIRMED -> next in listOf(ALLOTTED, CANCELLED)
            ALLOTTED -> next in listOf(DRIVER_ACCEPTED, REJECTED, CANCELLED)
            DRIVER_ACCEPTED -> next in listOf(DISPATCHED, ENROUTE, CANCELLED)
            DISPATCHED -> next in listOf(ENROUTE, ARRIVED, CANCELLED)
            ENROUTE -> next in listOf(ARRIVED, CANCELLED)
            ARRIVED -> next in listOf(STARTED, NO_SHOW, CANCELLED)
            STARTED -> next in listOf(IN_PROGRESS, COMPLETED)
            IN_PROGRESS -> next in listOf(COMPLETED)
            COMPLETED -> next in listOf(CLOSED)
            REJECTED -> next in listOf(ALLOTTED, CANCELLED)
            NO_SHOW -> next in listOf(CLOSED)
            CLOSED, CANCELLED -> false
        }
    }

    companion object {
        val activeSteps = listOf(
            BOOKED, CONFIRMED, ALLOTTED, DRIVER_ACCEPTED,
            DISPATCHED, ENROUTE, ARRIVED, STARTED,
            IN_PROGRESS, COMPLETED, CLOSED
        )
    }
}

enum class TripType(val label: String, val defaultKm: Int, val defaultHours: Int) {
    LOCAL_4HR_40KM("Local 4hr / 40km", 40, 4),
    LOCAL_8HR_80KM("Local 8hr / 80km", 80, 8),
    AIRPORT_PICKUP("Airport Pickup", 45, 2),
    AIRPORT_DROP("Airport Drop", 45, 2),
    OUTSTATION_ONE_WAY("Outstation One Way", 250, 8),
    OUTSTATION_ROUND_TRIP("Outstation Round Trip", 300, 12),
    HOURLY_RENTAL("Hourly Rental", 20, 2),
    FULL_DAY("Full Day 12hr / 120km", 120, 12),
    MULTI_DAY("Multi-Day Tour", 600, 48),
    EMPLOYEE_TRANSPORT("Employee Transport", 30, 2)
}

enum class VehicleCategory(val label: String, val basePriceRate: Double) {
    SEDAN("Executive Sedan", 1600.0),
    SUV("Premium SUV", 2400.0),
    PREMIUM_SEDAN("Luxury Sedan", 3200.0),
    PREMIUM_SUV("Luxury SUV", 4500.0),
    LUXURY("Ultra Luxury", 7500.0),
    TEMPO_TRAVELLER("Tempo Traveller (12+1)", 4200.0),
    BUS("Executive Mini Bus", 8500.0)
}
