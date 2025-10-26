## Stack Frame

Whenever a function is called in a program, its local variables and other function call data are stored in a stack frame within the stack segment of the application's memory. Each function gets its own stack frame.

The main components of a stack frame are:

* Method call information
* Local variables
* Method parameters
* Return address

A stack frame is composed of three parts:

```
Local Variables
Operand Stack
Frame Data
```

The sizes of the local variables and operand stack are determined at compile time and depend on the needs of each individual method. These sizes are included in the class file data. The size of the frame data is implementation-dependent.

### Local Variables Array (LVA)

The local variables section contains a method's parameters and local variables. Compilers place the parameters into the local variable array in the order they are declared.

* The local variable array is zero-based, and values are stored in words.
* int, float, reference, and return Address types occupy one entry each.
* byte, short, and char values are converted to int before being stored.
* long and double values occupy two consecutive entries in the array.
* To refer to a long or double, instructions use the index of the first entry in the pair.

```
For example, if a long occupies array entries 3 and 4, instructions would refer to that long by index 3.
```

### Operand Stack (OS)

The operand stack is a temporary workspace used by the Java Virtual Machine (JVM) for calculations. Unlike the local variables array, which is accessed using an index, the operand stack operates with special instructions. Values can be pushed to the operand stack and later popped for further operations.

* The operand stack stores the same data types as the local variables array: int, long, float, double, reference, and returnType.
* byte, short, and char values are converted to int before being pushed onto the operand stack.

```
For example, the JVM adds two integers by:

Pushing the first integer onto the operand stack.
Pushing the second integer onto the operand stack.
Using the add instruction to pop the two integers, add them, and push the result back.
```

### Frame Data (FD)

Frame data includes several components necessary for method invocation and execution:

* Constant Pool Resolution: Some JVM instructions use constant pool entries to refer to classes, arrays, fields, or methods. The frame data holds a pointer to the constant pool for resolution.

* Method Return: Upon normal method completion, the JVM restores the calling method’s stack frame and updates the program counter (pc) to the next instruction in the calling method.

* Exception Handling: The frame data contains a reference to the exception table, which helps the JVM determine which catch block should handle an exception. If no matching catch clause is found, the JVM restores the invoking method’s stack frame and rethrows the exception.

The frame data also ensures the JVM can handle both normal and abrupt method completions. For normal completion, if a method returns a value, it is pushed onto the operand stack of the calling method.