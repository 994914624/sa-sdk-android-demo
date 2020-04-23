package com.test.asmplugin

import org.gradle.model.internal.type.WildcardWrapper
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Handle
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter


class AutoClassVisitor extends ClassVisitor {

    // 类名
    private String className
    // 父类名
    private String superName
    // 实现的接口
    private String[] interfaces
    // 处理 lambda
    private HashMap lambdaMap =["lambda":[]]

    AutoClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM6, classVisitor)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.className = name
        this.superName = superName
        this.interfaces = interfaces
    }


    @Override
    FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, signature, value)
    }

    @Override
    void visitEnd() {
        super.visitEnd()
    }

    /**
     * 访问方法
     */
    @Override
     MethodVisitor visitMethod(int access, String name, String desc, String signature,
                                     String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)
        methodVisitor = new AdviceAdapter(Opcodes.ASM6, methodVisitor, access, name, desc) {

            @Override
            protected void onMethodEnter() {
                super.onMethodEnter()
//                if(className == "com/sensorsdata/analytics/android/sdk/SensorsDataAPI"&& name =="trackEvent"){
//                    println "-------onMethodEnter-------"+desc
//                }
                // hook config 指定的方法
                if (HookConfig.arrowsTarget.containsKey(className)) {
                    HookConfig.arrowsTarget.get(className).each {
                        matchInsert(it,name,desc,superName,interfaces,mv)
                    }
                }
                // 其它
                if(HookConfig.arrowsTarget.containsKey("*")){
                    HookConfig.arrowsTarget.get("*").each {
                        matchInsert(it,name,desc,superName,interfaces,mv)
                    }
                }
                // lambda
                if(lambdaMap.containsKey("lambda")){
                    lambdaMap.get("lambda").each {
                        matchInsert(it,name,desc,superName,interfaces,mv)
                    }
                }
            }

            @Override
            void visitInvokeDynamicInsn(String preName, String dynamicDesc, Handle bsm, Object... bsmArgs) {
                super.visitInvokeDynamicInsn(preName, dynamicDesc, bsm, bsmArgs)
                String preDesc = (String) bsmArgs[0]
//                println "------- pre ----------------"+preName+preDesc
                Handle afterHandle = (Handle) bsmArgs[1]
                // 处理后的 name & desc
//                println "------- afterHandle ----------------"+afterHandle.name +afterHandle.desc
                if(HookConfig.arrowsTarget.containsKey("*")){
                    ArrayList outList = HookConfig.arrowsTarget.get("*") as ArrayList
                    outList.each {
                        if(preName == it[0] && preDesc == it[1]){
                            ArrayList lambdaItem = it.clone() as ArrayList
                            // 存储处理后的 name
                            lambdaItem[0] = afterHandle.name
                            HashSet hashSet = lambdaMap.get("lambda") as HashSet
                            hashSet.add(lambdaItem)
                            lambdaMap.put("lambda",hashSet)
                        }
                    }
                }
            }

            @Override
            void visitEnd() {
                super.visitEnd()

            }

            @Override
            protected void onMethodExit(int i) {
                super.onMethodExit(i)
            }
        }
        return methodVisitor
    }

    /**
     * 条件匹配，插入代码
     */
    private matchInsert(def it, def name, def desc, def superName, def interfaces, def mv) {
        // if the matched method
        if (it[0] == name && it[1] == desc) {
            if (it[2] == null || superName.contains(it[2])) {
                if (it[3] == null || (interfaces != null && interfaces.contains(it[3]))) {
                    for (int num = it[-2];num <(it[-2]+it[-1]);num++) {
                        mv.visitVarInsn(Opcodes.ALOAD, num)// load 参数
                    }
                    println "$className.$name ----------> " + it[-5] + it[-4] + it[-3]
                    // 插入方法
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, it[-5], it[-4], it[-3], false)
                }
            }
        }
    }
}