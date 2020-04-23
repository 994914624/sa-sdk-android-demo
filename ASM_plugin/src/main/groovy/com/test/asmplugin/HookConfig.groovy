package com.test.asmplugin

class HookConfig {
    HashMap configMap
    // key=把包名的"."替换成"/"后的类名，value=[方法名，描述符，父类，接口，  插入的类名，插入的方法名，插入的描述符，load参数位置，数量]
    static def arrowsTarget = [:]

    // 全限定名：把包名的"."替换成"/"
    // 描述符：格式为"(参数类型列表)返回值类型"
    // 参数类型为：全限定名，为了使连续的多个全限定名之间不产生混淆，在使用时最后一般会加入一个";"表示全限定名结束。如，(Landroid/view/View;)V
    // 返回值类型：（void -> V，boolean -> Z，long -> J，int -> I，double -> D，char ->C，float -> F，byte -> B，对象类型 -> L；如 Ljava/lang/Object）
    // 参数位置：从 0 开始，非静态方法 0 为 this


    static {
//        // TD hook config
//        arrowsTarget.put("com/tendcloud/tenddata/TCAgent", [
//                ["onEvent", "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/util/Map;)V", "java/lang/Object", null,
//                 "cn/sa/demo/utils/SensorsDataUtil", "onEvent", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/Map;)V", 1, 3
//                ],
//                ["onPageStart", "(Landroid/content/Context;Ljava/lang/String;)V", "java/lang/Object", null,
//                 "cn/sa/demo/utils/SensorsDataUtil", "onPageStart", "(Ljava/lang/String;)V", 1, 1
//                ],
//                ["onPageEnd", "(Landroid/content/Context;Ljava/lang/String;)V", "java/lang/Object", null,
//                 "cn/sa/demo/utils/SensorsDataUtil", "onPageEnd", "(Ljava/lang/String;)V", 1, 1
//                ]
//        ])
        // * 表示类名不做限
        arrowsTarget.put("*", [
                // hook onClick 点击
                ["onClick", "(Landroid/view/View;)V", null, "android/view/View\$OnClickListener",
                 "cn/sa/demo/utils/hook2/AutoUtil", "onClick", "(Landroid/view/View;)V", 1, 1
                ],
                // Activity onResume
                ["onResume", "()V", "/app/AppCompatActivity", null,
                 "cn/sa/demo/utils/hook2/AutoUtil", "onResume", "(Landroid/app/Activity;)V", 0, 1
                ]
        ])

        // hook trackEvent
        arrowsTarget.put("com/sensorsdata/analytics/android/sdk/SensorsDataAPI",[
                ["trackEvent", "(Lcom/sensorsdata/analytics/android/sdk/EventType;Ljava/lang/String;Lorg/json/JSONObject;Ljava/lang/String;)V", null, null,
                 "cn/sa/demo/utils/hook2/AutoUtil", "trackEvent", "(Ljava/lang/Object;Ljava/lang/String;Lorg/json/JSONObject;Ljava/lang/String;)V", 1, 4
                ]
        ])
    }
}