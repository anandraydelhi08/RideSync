package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.demo.DemoDataGenerator
import com.example.data.model.DutyStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("VeloFleet", appName)
  }

  @Test
  fun `duty status transition validation`() {
    assertTrue(DutyStatus.BOOKED.canTransitionTo(DutyStatus.CONFIRMED))
    assertTrue(DutyStatus.CONFIRMED.canTransitionTo(DutyStatus.ALLOTTED))
    assertTrue(DutyStatus.ALLOTTED.canTransitionTo(DutyStatus.DRIVER_ACCEPTED))
    assertTrue(DutyStatus.ARRIVED.canTransitionTo(DutyStatus.STARTED))
    assertTrue(DutyStatus.STARTED.canTransitionTo(DutyStatus.COMPLETED))
  }

  @Test
  fun `demo data generation contains complete entities`() {
    val bookings = DemoDataGenerator.generateBookings()
    val drivers = DemoDataGenerator.generateDrivers()
    val vehicles = DemoDataGenerator.generateVehicles()

    assertTrue(bookings.size >= 10)
    assertTrue(drivers.size >= 10)
    assertTrue(vehicles.size >= 10)
  }
}
