package bugs.decentralized.utils

fun Long.epsilonEquals(other: Long, epsilon: Long) = other in this - epsilon..this + epsilon
typealias StringMap = Map<String, String>