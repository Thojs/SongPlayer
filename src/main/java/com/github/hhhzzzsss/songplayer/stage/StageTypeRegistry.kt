package com.github.hhhzzzsss.songplayer.stage

class StageTypeRegistry private constructor() {
    private val types = HashMap<String, StageType>()

    fun registerStageTypes(vararg types: StageType) {
        for (type in types) {
            if (type.identifier.contains(" ")) continue  // Spaces are not allowed.

            this.types[type.identifier] = type
        }
    }

    fun getType(identifier: String): StageType? {
        return types[identifier]
    }

    val identifiers: List<String>
        get() = types.keys.toList()

    companion object {
        @JvmField
        val instance: StageTypeRegistry = StageTypeRegistry()
    }
}
