package net.uiqui.oauth.mock.tools

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class HolderTest {
    @Test
    fun `test holder`() {
        // given
        val classUnderTest = Holder<Int>(0)
        // when
        classUnderTest.value = 123
        // then
        assertThat(classUnderTest.value).isEqualTo(123)
    }
}
