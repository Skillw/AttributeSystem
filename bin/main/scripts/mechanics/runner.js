KetherShell = find(">taboolib.module.kether.KetherShell").INSTANCE;

// 运行kether
//@Mechanic(kether)
function runner(data, context, damageType) {
    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        return 0.0;
    }
    const run = data.handle(context.get("run"));
    return KetherShell.eval(
        run,
        true,
        listOf(),
        KetherShell.mainCache,
        null,
        null,
        function (a) {
        }
    );
}

// 运行内联函数
//@Mechanic(function)
function func(data, context, damageType) {
    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        return 0.0;
    }
    const run = data.handle(context.get("run"));
    return Pouvoir.pouFunctionManager.parse(run, arrayOf("common"), data);
}
