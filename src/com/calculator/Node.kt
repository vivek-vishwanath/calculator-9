package com.calculator

import java.lang.RuntimeException
import java.lang.StringBuilder
import kotlin.math.pow

internal class Node(var value: String) {

    private val exception = RuntimeException("Error in calculation")

    val children = ArrayList<Node>()

    var parent: Node? = null

    fun evaluate(): Double = when {
        value.isNumber() -> value.toDouble()
        value.isOperator() -> operate(value,this) ?: throw exception
        value.isOpen() || value.isParentheses() -> this[0].evaluate()
        value.isWord() ->
            if (children.size == 1 && (this[0].value.isOpen() || this[0].value.isParentheses())) {
                val values = DoubleArray(this[0].children.size) { this[0][it].evaluate() }
                value.function(values)
            } else throw exception
        else -> throw exception
    }

    fun operate(operator: String, node: Node): Double? {
        assert(operator.isOperator())
        if (node.children.size == 1 && node.value == "-") return node[0].evaluate().unaryMinus()
        val n1 = node[0].evaluate()
        val n2 = node[1].evaluate()
        return when (operator) {
            "+" -> n1 + n2
            "-" -> n1 - n2
            "*" -> n1 * n2
            "/" -> n1 / n2
            "^" -> n1.pow(n2)
            else -> null
        }
    }

    fun addChild(node: Node): Node {
        node.parent = this
        children.add(node)
        return node
    }

    fun insertAbove(node: Node): Node {
        if (parent != null) {
            parent!!.replace(this, node)
            node.parent = parent
            parent = node
        }
        node.addChild(this)
        return node
    }

    operator fun get(index: Int) = children[index]

    operator fun set(index: Int, value: Node) {
        children[index] = value
    }

    private fun replace(old: Node, new: Node) {
        this[children.indexOf(old)] = new
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(value)

        for (node in children) {
            val hasNext = node != children.last()
            node.traverseNodes(builder, "", if (hasNext) "├──" else "└──", hasNext)
        }
        return builder.toString()
    }

    private fun traverseNodes(builder: StringBuilder, padding: String, pointer: String, hasSibling: Boolean) {
        builder.append("\n").append(padding).append(pointer).append(value)

        val paddingBuilder = StringBuilder(padding)
        paddingBuilder.append(if (hasSibling) "│  " else "   ")

        for (node in children) {
            val hasNext = node != children.last()
            node.traverseNodes(builder, paddingBuilder.toString(), if (hasNext) "├──" else "└──", hasNext)
        }
    }

}
