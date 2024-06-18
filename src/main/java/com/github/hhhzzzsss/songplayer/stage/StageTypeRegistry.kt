package com.github.hhhzzzsss.songplayer.stage

object StageTypeRegistry {
    private val types = HashMap<String, StageType>()

    @JvmStatic
    fun registerStageTypes(vararg types: StageType) {
        for (type in types) {
            if (type.identifier.contains(" ")) continue  // Spaces are not allowed.

            this.types[type.identifier] = type
        }
    }

    @JvmStatic
    fun getType(identifier: String): StageType? {
        return types[identifier]
    }

    val identifiers: List<String>
        get() = types.keys.toList()
}
