### Python Basics

1. Python uses interpreter to convert the code to machine understandable instructions.
2. Best practice is always to use single quotes in print function in Python
3. In python there cannot be more than one instruction in a line
4. By default, print() function provides new line character at the end by default.

5. In Python, variable names must start with a letter or an underscore sign
6. Variable name cannot start with numbers
7. In Python, from and global cannot be used a variable names.
8. Variable data types: string, integer, float, boolean
9. Boolean value should start with upper case letter - Only True or False are valid booleans
10. In python, comments are started with a hash (#) sign.

11. Beginning python3.6, we can use underscores in numbers: e.g: 2_333_333 == 2333333
12. Decimal Numbers can also be represented in scientific notation (for e.g 3e4 for 30000.0, 3e-4 for 0.0003) => 3e4 = 3 * 10 pow 4 > 3 *10000
13. In Python, Numbers can also be represented in octal and in hexadecimal format
14. If any number  starts with 0O or 0o, then the number is in octal value. After the 0O or 0o, we can give only from 1 to 7 numbers. 8 and 9 are not allowed.
15. Hexadecimal numbers starts with 0X or 0x
16. In Python, the print() function will automatically convert the octal and hex decimal numbers into Numbers

17. `+ (plus), - (minus), * (multiplication), / (division), // (integer division) and % (Modulus division)` are the operators available in Python.
18. `/ (division)` produces float output
19. `// (integer division)` produces the nearest whole integer output instead of having decimals in the number unlike / (division) operator.
20. `% (modulus division)` produces the reminder as output  > 3%4 ==> 3; 3 %5 ==> 3; 4%2 ==> 0
21. We can use `+=, -=, *=` as operators: age += 1 (age = age+1)
22. `**` is `the power operator` in Python, e.g: 3 **3 =27, 3*3*3 =27
23. We can multiply a string with number in python (e.g: `test*2` will output `testtest`)
24. Python is an interpreter language, we need an interpreter to converting it to machine code.
25. Python interpreter reads code from top to bottom.
26. Interpreter checks the following while reading the code - Lexis, Syntax and Semantics
27. Java is a compiler language

28. We can use two functions in a single line
29. Typecasting functions: `int(), float(), str()`
30. `input()` function reads any value in string type
31. We required to typecast the `intput(`) output numbers before using it for mathematical calculation purpose.
32. Order of operators:
     1. `**`
     2. `* / // %`
     3. `+ -`
     * 1 // 2 * 3 = 0 => first evaluate 1//2 which is 0 and then 0*3 which is 0: Division and multiplication takes equal precedence from left to right.
33. The `BODMAS` rule is a mnemonic for the order of operations in mathematics: Brackets, Orders (powers/roots), Division, Multiplication, Addition, and Subtraction, ensuring correct calculation sequences. Crucially, Division and Multiplication are of equal priority and solved left-to-right.
34. In python, exponentiation operator takes right sided binding, (I.e. it starts from right) > 2**3**2 ==> 2**9 = 512; 3 **2**2 ==> 3**4 =81
35. Keyword Arguments/Named Arguments: e.g: `end=‘.’, sep=‘.’`
36. The default end argument is new line character.
37. The default space argument is space character.


1. Bitwise operations on Python are possible but almost never used.
2. In python, there are 6 bitwise operators (&, |, ^, ~, <<, >>)
3. `~` is logical negation (~x = -x-1) which is logical not > ~1 =-1-1 ==> -2
4. `<<` operator:  (left shift)
    1. 3 << 1 = 3 * 2 = 6
    2. 3 << 2=3*(2*2) = 12
    3. 3 << 3=3 * (2*2*2) = 24
5. `>>` operator: (right shift)
    1. 12 >> 1 = 12/2 = 6
    2. 12>>2 = 12/(2*2)= 3
6. In Python, 2 is equal to 2.0
7. not, and, or are the multiple condition joining operators,
8. These boolean operators has the priority
    1. not
    2. and
    3. or
9. We can use `\` (backslash) to code in multiline in python
    1. E.g: (elif not user_age >= 23 and user_country == 'def' or  user_country == 'ghi' \
    2.    or user_country == 'jkl':)
10. In python triple quotes (‘’’) at the start and end of the string in print will allow the string to print in multiple lines
    1. print(‘’’ ==============
       1.     == print==
       2. ==============‘’’)
11. Sequence is a type of data structure in python which stores multiple values of same type.
    1. E.g: string is a sequence of multiple characters: ‘Hello’ => h, e, l, l, 0
12. The index in for loop is called control variable
    1. e.g: for i in ‘hello’ => here i is control variable.
    2. E.g: for index in range(1, 11): ==> here in the range function, start value 1 is inclusive and the end value 11 is exclusive.
13. break and continue instructions will break and skip the iterations in loop respectively.
14. pass instruction will do nothing in the loop
    1. for i in range(11): here the pass will do nothing and for loop will not give any error during runtime,. Here range is from 0 to 10 (11 is exclusive)
        1. pass
15. In python, while and for loops will have else branch:  The else branch of a while/for loop is always executed exactly once except in break, continue and pass statement/instruction scenario.'''

Next: [Python Collections](./python_collections.md)