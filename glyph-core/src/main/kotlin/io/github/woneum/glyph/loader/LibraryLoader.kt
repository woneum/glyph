package io.github.woneum.glyph.loader

import org.bukkit.Bukkit
import java.lang.reflect.InvocationTargetException

object LibraryLoader {
    fun <T> loadNMS(clazz: Class<T>, vararg initArgs: Any? = emptyArray()): T {
        val packageName = clazz.`package`.name
        val className = "NMS${clazz.simpleName}"
        val parameterTypes = initArgs.map {
            it?.javaClass
        }.toTypedArray()

        val candidates = ArrayList<String>(2)
        candidates.add("$packageName.$libraryVersion.$className")

        val lastDot = packageName.lastIndexOf('.')
        if (lastDot > 0) {
            val superPackageName = packageName.substring(0, lastDot)
            val subPackageName = packageName.substring(lastDot + 1)
            candidates.add("$superPackageName$libraryVersion$subPackageName$className")
        }

        return try {
            val nmsClass = candidates.firstNotNullOfOrNull { candidate ->
                try {
                    Class.forName(candidate, true, clazz.classLoader).asSubclass(clazz)
                } catch (exception: ClassNotFoundException) {
                    null
                }
            } ?: throw ClassNotFoundException("Not found nms library class: $candidates")
            val constructor = kotlin.runCatching {
                nmsClass.getConstructor(*parameterTypes)
            }.getOrNull()
                ?: throw UnsupportedOperationException("${clazz.name} does not have Constructor for [${parameterTypes.joinToString()}]")
            constructor.newInstance(*initArgs) as T
        } catch (exception: ClassNotFoundException) {
            throw UnsupportedOperationException(
                "${clazz.name} does not support this version: $libraryVersion",
                exception
            )
        } catch (exception: IllegalAccessException) {
            throw UnsupportedOperationException("${clazz.name} constructor is not visible")
        } catch (exception: InstantiationException) {
            throw UnsupportedOperationException("${clazz.name} is abstract class")
        } catch (exception: InvocationTargetException) {
            throw UnsupportedOperationException(
                "${clazz.name} has an error occurred while creating the instance",
                exception
            )
        }
    }

    val minecraftVersion by lazy {
        Bukkit.getServer().minecraftVersion
    }

    val libraryVersion by lazy { "v${minecraftVersion.replace('.', '_')}" }
}