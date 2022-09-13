package bugs.decentralized.utils

typealias StringMap = HashMap<String, String>

fun Long.epsilonEquals(other: Long, epsilon: Long) = other in this - epsilon..this + epsilon