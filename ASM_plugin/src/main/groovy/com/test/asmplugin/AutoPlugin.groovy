package com.test.asmplugin;

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES


class AutoPlugin extends Transform implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def android = project.extensions.getByType(AppExtension)
        // 扩展属性
        HookConfig extension = project.extensions.create("hookConfig", HookConfig)
        //注册全局的 Transform
        android.registerTransform(this)
        project.afterEvaluate {
            try {
                // 添加扩展的配置
                if (extension.configMap != null) {
                    HashMap hashMap = extension.configMap as Map
                    println "\033[47;35m 您 build.gradle 配置的 hookConfig 为："+hashMap
                    hashMap.each {key, value ->
                        ArrayList out = value as ArrayList
                        if(out.size()<=0){
                            throw new Exception("outList.size()<=0\n")
                        }
                        value.each {
                            ArrayList itemList = it as ArrayList
                            if(itemList.size()!=9){
                                throw new Exception("itemList.size()!=9\n")
                            }
                        }
                    }
                    if (hashMap.containsKey("*")) {
                        ArrayList arrayList = hashMap.get("*") as ArrayList
                        ArrayList arrayList2 = HookConfig.arrowsTarget.get("*") as ArrayList
                        if (arrayList != null && arrayList2 != null) {
                            arrayList.addAll(arrayList2)
                        }
                        hashMap.put("*", arrayList)
                    }
                    HookConfig.arrowsTarget.putAll(hashMap)
                }
            } catch(Exception e){
                throw new Error("\033[47;35m build.gradle 中 hookConfig 配置不正确 ！！！"+e.getMessage())

            }
        }
    }

    @Override
    String getName() {
        return "testASM"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        def startTime = System.currentTimeMillis()
        println("transform start time ---->：" + startTime.toString())
        inputs.each { TransformInput input ->
            // 遍历 Directory input
            input.directoryInputs.each { DirectoryInput directoryInput ->
                if (directoryInput.file.isDirectory()) {
                    directoryInput.file.eachFileRecurse { File file ->
                        def name = file.name
                        if (name.endsWith(".class") && !name.startsWith("R\$") &&
                                !"R.class".equals(name) && !"BuildConfig.class".equals(name)) {
                            println(name + " is visit")
                            ClassReader cr = new ClassReader(file.bytes)
                            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                            ClassVisitor cv = new AutoClassVisitor(cw)

                            cr.accept(cv, ClassReader.EXPAND_FRAMES)

                            byte[] code = cw.toByteArray()
                            FileOutputStream fos = new FileOutputStream(file.parentFile.absolutePath + File.separator + name)
                            fos.write(code)
                            fos.close()
                        }
                    }
                }
                // output 目录
                def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                // copy 到指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            // 遍历第三方 jar
            input.jarInputs.each { JarInput jarInput ->
                // 重名名输出文件,因为可能同名,会覆盖
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }

                File tmpFile = null
                if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
                    JarFile jarFile = new JarFile(jarInput.file)
                    Enumeration enumeration = jarFile.entries()
                    tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_trace.jar")

                    if (tmpFile.exists()) {//避免上次的缓存 File 被重复插入
                        tmpFile.delete()
                    }
                    JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
                    //用于保存
                    ArrayList<String> processorList = new ArrayList<>()
                    while (enumeration.hasMoreElements()) {//遍历 jar 中每个 file
                        JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                        String entryName = jarEntry.getName()
                        ZipEntry zipEntry = new ZipEntry(entryName)
                        InputStream inputStream = jarFile.getInputStream(jarEntry)

                        // 操作 class
                        if (entryName.endsWith(".class") && !entryName.contains("R\$") &&
                                !entryName.contains("R.class") && !entryName.contains("BuildConfig.class")) {
                            //Begins writing a new JAR file entry
                            jarOutputStream.putNextEntry(zipEntry)
                            ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                            def className = entryName.split(".class")[0]
                            ClassVisitor cv = new AutoClassVisitor(classWriter)
                            classReader.accept(cv, EXPAND_FRAMES)
                            byte[] code = classWriter.toByteArray()
                            //Writes an array of bytes to the current ZIP entry data.
                            jarOutputStream.write(code)

                        } else if (entryName.contains("META-INF/services/javax.annotation.processing.Processor")) {
                            if (!processorList.contains(entryName)) {
                                processorList.add(entryName)
                                jarOutputStream.putNextEntry(zipEntry)
                                jarOutputStream.write(IOUtils.toByteArray(inputStream))
                            } else {
                                println "duplicate entry:" + entryName
                            }
                        } else {

                            jarOutputStream.putNextEntry(zipEntry)
                            jarOutputStream.write(IOUtils.toByteArray(inputStream))
                        }

                        jarOutputStream.closeEntry()
                    }

                    // 操作 class 结束
                    jarOutputStream.close()
                    jarFile.close()
                }

                // output 目录
                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                if (tmpFile == null) {
                    // copy 没有做操作的文件
                    FileUtils.copyFile(jarInput.file, dest)
                } else {
                    // copy 做了操作后的文件
                    FileUtils.copyFile(tmpFile, dest)
                    tmpFile.delete()
                }
            }
        }
        println("ASM transform take time ---->：" + (System.currentTimeMillis() - startTime).toString())
    }
}
