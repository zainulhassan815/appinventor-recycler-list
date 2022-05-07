package com.dreamers.recyclerlist.utils

import android.util.Log
import com.google.appinventor.components.runtime.Form
import com.google.appinventor.components.runtime.ReplForm
import com.google.appinventor.components.runtime.errors.IllegalArgumentError
import gnu.lists.LList
import gnu.mapping.ProcedureN
import gnu.mapping.SimpleSymbol
import gnu.mapping.Symbol
import kawa.standard.Scheme

class FunctionInvoker(private val form: Form) {

    private val proceduresCache: MutableMap<String, ProcedureN> = mutableMapOf()

    // Procedures defined using the def syntax defined in runtime.scm on line 665. The def macro
    // behaves differently in the REPL (Companion) versus a compiled app. The following two methods
    // provide the appropriate lookup behavior depending on whether this extension is being used in
    // the REPL environment or in the compiled environment.
    private fun lookupProcedureInCompanion(procedureName: String): ProcedureN? {
        val lang = Scheme.getInstance()
        try {
            // Since we're in the REPL, we can cheat and invoke the Scheme interpreter to get the method.
            val result = lang.eval("(begin (require <com.google.youngandroid.runtime>)(get-var p$$procedureName))")
            if (result is ProcedureN) {
                return result
            } else {
                throwError(message = "Wanted a procedure, but got a ${result?.javaClass?.toString() ?: "null"}")
            }
        } catch (throwable: Throwable) {
            throwError(throwable.message.toString(), throwable)
            throwable.printStackTrace()
        }
        return null
    }

    private fun lookupProcedureInForm(procedureName: String): ProcedureN? {
        try {
            val globalVarEnvironment = form.javaClass.getField("global\$Mnvars\$Mnto\$Mncreate")
            val vars = globalVarEnvironment[form] as LList
            val procSym: Symbol = SimpleSymbol("p$$procedureName")
            var result: Any? = null
            for (pair in vars) {
                if (LList.Empty != pair) {
                    val asPair = pair as LList
                    if ((asPair[0] as Symbol?)?.name == procSym.name) {
                        result = asPair[1]
                        break
                    }
                }
            }
            if (result is ProcedureN) {
                // The def syntax wraps the function definition in an additional lambda, which we evaluate
                // here so that the return value of this is the lambda implementing the blocks logic.
                // See runtime.scm#665
                return result.apply0() as ProcedureN
            } else {
                throwError("Wanted a procedure, but got a ${result?.javaClass?.toString() ?: "null"}")
            }
        } catch (throwable: Throwable) {
            throwError(throwable.message.toString(), throwable)
            throwable.printStackTrace()
        }
        return null
    }

    fun invoke(procedureName: String, arguments: List<Any?>?): Any? {
        val procedure = lookupProcedure(procedureName)
        return call(procedure, arguments)
    }

    fun lookupProcedure(procedureName: String): ProcedureN {
        return if (proceduresCache.contains(procedureName)) {
            proceduresCache[procedureName]!!
        } else {
            val procedure = if (form is ReplForm) {
                lookupProcedureInCompanion(procedureName)
            } else {
                lookupProcedureInForm(procedureName)
            }
            if (procedure != null) proceduresCache[procedureName] = procedure
            procedure ?: throw IllegalArgumentError("Unable to locate procedure $procedureName in form $form")
        }
    }

    fun call(procedure: ProcedureN, arguments: List<Any?>?): Any? {
        return try {
            if (arguments == null || procedure.numArgs() == 0) {
                procedure.apply0()
            } else {
                val argArray = arrayOfNulls<Any>(arguments.size)
                var i = 0
                val it: Iterator<*> = arguments.iterator()
                while (it.hasNext()) {
                    argArray[i++] = it.next()
                }
                procedure.applyN(argArray)
            }
        } catch (throwable: Throwable) {
            throwError("an unknown error occurred", throwable)
            throwable.printStackTrace()
        }
    }

    private fun throwError(message: String, throwable: Throwable? = null) {
        Log.e("RecyclerList", "Function Invoker : $message", throwable)
    }

}