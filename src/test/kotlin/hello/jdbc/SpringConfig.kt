package hello.jdbc

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringExtension

object SpringConfig : AbstractProjectConfig() {
    /* override val parallelism = 3
     override val assertionMode = AssertionMode.Error
     override val globalAssertSoftly = true
     override val failOnIgnoredTests = false*/
    override val isolationMode = IsolationMode.InstancePerLeaf
    override fun extensions() = listOf(SpringExtension)
}
