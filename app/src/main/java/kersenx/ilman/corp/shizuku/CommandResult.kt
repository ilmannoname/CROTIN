package kersenx.ilman.corp.shizuku

data class CommandResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String
) {
    val isSuccess: Boolean get() = exitCode == 0
}
