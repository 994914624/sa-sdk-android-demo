package com.test.asmplugin

import org.gradle.model.internal.type.WildcardWrapper
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter


class AutoClassVisitor extends ClassVisitor {

    /**
     * 类名
     */
    private String className

    /**
     * 父类名
     */
    private String superName

    /**
     * 该类实现的接口
     */
    private String[] interfaces

    public AutoClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM6, classVisitor)
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
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
    public MethodVisitor visitMethod(int access, String name, String desc, String signature,
                                     String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)
        methodVisitor = new AdviceAdapter(Opcodes.ASM6, methodVisitor, access, name, desc) {

            @Override
            protected void onMethodEnter() {
                super.onMethodEnter()
            }

            @Override
            protected void onMethodExit(int i) {
                super.onMethodExit(i)
                // hook config 指定的方法
                if (HookConfig.arrowsTarget.containsKey(className)) {
                    HookConfig.arrowsTarget.get(className).each {
                        matchInsert(it,name,desc,superName,interfaces,mv)
                    }
                    return
                }
                // 其它
                HookConfig.arrowsTarget.get("*").each {
                    matchInsert(it,name,desc,superName,interfaces,mv)
                }

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