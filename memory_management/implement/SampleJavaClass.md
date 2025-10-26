Let us find out the memory allocation for teh following sample java class

```
public class SampleJavaClass {

    private static int sum = 0;

    public static void main(String [] args) {
        
        SampleJavaClass sampleJavaClass = new SampleJavaClass();
        sum = sampleJavaClass.addition(10, 20);

        System.out.println("sum is : "+sum);
    }

    private int addition(int a, int b) {
        return a + b;
    }
}

```

### 1. Class (SampleJavaClass)

Memory Type: Method Area (Part of Heap)

Details:

The class definition (including static fields and methods) is loaded into the Method Area of the memory when the class is loaded by the JVM.

The method area holds class-level data such as the bytecode of the class, static variables, and method references.

### 2. Static Variable (sum)

Memory Type: Heap Memory (Method Area)

Details:

The static variable sum belongs to the class, not to any specific instance of the class.

Static fields are stored in the Method Area (or a part of the Heap dedicated to class-related data), meaning it's shared across all instances of the class. It’s created and initialized when the class is loaded into memory.

### 3. Local Variable (args)

Memory Type: Stack Memory

Details:

args is a parameter in the main method, and local variables within methods are stored in Stack Memory.

When the method main is called, space for args is allocated on the stack and deallocated when the method execution ends.

### 4. Local Variable (sampleJavaClass)

Memory Type: Stack Memory

Details:

The reference variable sampleJavaClass is a local variable in the main method.

It will be created on the stack when the method is executed.

The reference itself (the address pointing to the object) is stored in the stack.

### 5. Object (new SampleJavaClass())

Memory Type: Heap Memory

Details:

The object instance of SampleJavaClass created by new SampleJavaClass() is allocated in Heap Memory.

The object is created dynamically during runtime, and its instance variables are also part of this memory.

### 6. Instance Method (addition)

Memory Type: Method Area

Details:

The method addition is part of the Method Area since it is a class-level method.

The actual method code is stored in the method area when the class is loaded into memory.

### 7. Method Parameters (a and b in addition)

Memory Type: Stack Memory

Details:

The parameters a and b for the method addition(int a, int b) are local variables for the method.

They exist on the stack during the method’s execution, and once the method completes, they are removed from the stack.

-----------------------------------------------------------------------------------------------------------------------------------------

### Summary of Memory Types:

```

| Variable/Method                    | Memory Type               | Explanation                                                |
| ---------------------------------- | ------------------------- | ---------------------------------------------------------- |
| `SampleJavaClass` (class)          | Method Area (Heap)        | Class data, static variables, and methods are here.        |
| `sum` (static variable)            | Heap Memory (Method Area) | Static variables belong to the class, not instances.       |
| `args` (parameter)                 | Stack Memory              | Local variables and parameters are stored in the stack.    |
| `sampleJavaClass` (local variable) | Stack Memory              | Reference variable for the object is stored on the stack.  |
| `new SampleJavaClass()` (object)   | Heap Memory               | Object instance created at runtime is stored in the heap.  |
| `addition` (method)                | Method Area               | The method code is loaded into the method area.            |
| `a`, `b` (method parameters)       | Stack Memory              | Local variables for method execution, stored in the stack. |

```