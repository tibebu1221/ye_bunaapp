package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.ProjectCard
import com.example.ui.Project
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleProject = Project(
        id = 1,
        title = "Bunna Brew Branding",
        category = "Brand Identity",
        freelancerName = "Almaz Kebede",
        description = "Organic visual identity for a heritage coffee roaster, featuring hand-carved textures.",
        longDescription = "A complete brand transformation tracing organic highland roots.",
        rating = 4.9,
        client = "Bunna Brew Roasters Ltd.",
        year = "2026",
        tools = listOf("Adobe Illustrator", "Figma")
    )
    composeTestRule.setContent {
        MyApplicationTheme {
            ProjectCard(project = sampleProject, onClick = {})
        }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
