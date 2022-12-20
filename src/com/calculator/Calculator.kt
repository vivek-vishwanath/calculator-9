package com.calculator

import java.lang.RuntimeException
import java.lang.StringBuilder
import java.util.*

object Calculator {

    /**
     * Parses @param[expression] in the format of a String and simplifies it into a double value
     * @return the double value of the simplified expression
     */
    fun calculate(expression: String): Double {
        val tokens = tokenize(expression)
        val tree = buildTree(tokens)
        return tree.evaluate()
    }

    /**
     * Parses @param[string] into a linked list of tokens (i.e. numbers, operators, functions, parentheses, commas)
     * @return list of tokens
     */
    private fun tokenize(string: String): LinkedList<String> {
        val queue = LinkedList<String>()
        var previous = '.'
        var current = StringBuilder()
        for (c in string) {
            if ((!previous.isDigit() || !c.isDigit()) && (!previous.isLetter() || !c.isLetter())) {
                queue.add(current.toString())
                current = StringBuilder()
            }
            current.append(c)
            previous = c
        }
        queue.add(current.toString())
        queue.pop()
        println(queue)
        return queue
    }

    /**
     * Given a @param[queue] of tokens, it constructs a non-binary tree of nodes.
     * Each node stores a token and points to its children and parent
     * @return the root node of the constructed tree
     */
    private fun buildTree(queue: LinkedList<String>): Node {
        val root = Node(queue.pop())
        var pointer = root
        while (queue.size > 0) {
            val token = queue.pop()
            val node = Node(token)
            val previous = pointer.value
            val exception = RuntimeException("Invalid tokens together: '$previous' and '$token'")
            pointer = when {
                previous.isNumber() -> when {
                    token.isOperator() -> placeOperator(pointer, token, node)
                    token.isOpen() || token.isWord() -> impliedMultiply(pointer, node)
                    token.isClose() -> closeParentheses(pointer)
                    token.isComma() -> upToParentheses(pointer)
                    else -> throw exception
                }
                previous.isOperator() || previous.isOpen() -> pointer.addChild(node)
                previous.isParentheses() -> when {
                    token.isOperator() -> placeOperator(pointer, token, node)
                    token.isClose() -> closeParentheses(pointer)
                    token.isWord() -> impliedMultiply(pointer, node)
                    token.isComma() -> upToParentheses(pointer)
                    else -> throw exception
                }
                previous.isWord() -> when {
                    token.isOpen() -> pointer.addChild(node)
                    else -> throw exception
                }
                else -> throw exception
            }
        }
        while (pointer.parent != null) pointer = pointer.parent!!
        return pointer
    }

    /**
     * Auxiliary method for [buildTree]
     * As it loops through the queue of tokens, when it encounters an operator,
     * this method places that operator appropriately in the tree
     */
    private fun placeOperator(p: Node, token: String, node: Node): Node {
        var pointer = p
        while (pointer.parent != null) {
            if (precedence(token) > precedence(pointer.parent!!.value)) break
            pointer = pointer.parent!!
        }
        if (pointer.value.isParentheses() && pointer.parent != null && pointer.parent!!.value.isWord()) pointer = pointer.parent!!
        return pointer.insertAbove(node)
    }

    /**
     * Auxiliary method for [buildTree]
     * As it loops through the queue of tokens,
     * this method moves the pointer up the tree until it finds parentheses
     */
    private fun upToParentheses(p: Node): Node {
        var pointer = p
        while (pointer.value != "(")
            if (pointer.parent != null)
                pointer = pointer.parent!!
            else {
                pointer.insertAbove(Node("("))
                pointer = pointer.parent!!
                break
            }
        return pointer
    }

    /**
     * Auxiliary method for [buildTree]
     * This method provides the closing parenthesis ')' for a node with an existing opening parenthesis '('
     */
    private fun closeParentheses(p: Node): Node {
        val pointer = upToParentheses(p)
        pointer.value += ")"
        return pointer
    }

    /**
     * Auxiliary method for [buildTree]
     * This method accounts for situations with an implied multiplication
     * This happens when an opening parenthesis '(' appears after a number or function
     */
    private fun impliedMultiply(pointer: Node, node: Node): Node {
        pointer.insertAbove(Node("*"))
        return pointer.parent!!.addChild(node)
    }
}
