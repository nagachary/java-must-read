# Java DSA Time & Space Complexity — Complete Interview Reference

> Java 8+ interview reference | Operations, patterns, algorithms, worked examples, and a repeatable complexity-analysis method.

## Contents
1. [Notation and definitions](#1-notation-and-definitions)
2. [How to calculate complexity for any solution](#2-how-to-calculate-complexity-for-any-solution)
3. [Worked complexity analyses](#3-worked-complexity-analyses)
4. [Java data structures: operation-by-operation](#4-java-data-structures-operation-by-operation)
5. [Sorting and searching](#5-sorting-and-searching)
6. [Trees and graphs](#6-trees-and-graphs)
7. [Core coding patterns](#7-core-coding-patterns)
8. [Dynamic programming and backtracking](#8-dynamic-programming-and-backtracking)
9. [Strings, matrices, bits, and math](#9-strings-matrices-bits-and-math)
10. [Advanced data structures](#10-advanced-data-structures)
11. [Java-specific complexity traps](#11-java-specific-complexity-traps)
12. [Interview cheat sheets and practice](#12-interview-cheat-sheets-and-practice)

---

## 1. Notation and definitions

- **Time complexity** describes how running work grows with input size.
- **Space complexity** describes memory consumption as input size grows. In interviews, explicitly say whether you mean **auxiliary space** (extra memory excluding input and often excluding required output) or **total space**.
- **Worst case**: maximum work for an input of the specified size.
- **Average/expected case**: work averaged over an explicitly assumed distribution or randomness.
- **Amortized cost**: average cost per operation over a sequence, even if an occasional operation is expensive.
- **n, m**: input lengths; **V, E**: vertices and edges; **h**: tree height; **k**: result count or window size; **W**: capacity; **A**: target amount; **L**: string/key length.

| Class | Typical example | Interpretation |
|---|---|---|
| O(1) | `arr[i]` | Constant work |
| O(log n) | Binary search | Halve the remaining candidates |
| O(√n) | Trial-division primality | Check divisors up to square root |
| O(n) | One traversal | Work proportional to n |
| O(n log n) | Merge sort | log n levels, n work per level |
| O(n²) | All pairs | Nested independent scans |
| O(n³) | Floyd–Warshall | Three independent dimensions |
| O(2^n) | Enumerating subsets | Two choices per element |
| O(n!) | Enumerating permutations | All orderings |

**Simplification rules:** Ignore constant factors (`O(3n)` → `O(n)`); keep dominant terms (`O(n²+n)` → `O(n²)`); retain independent variables (`O(n+m)` does **not** become `O(n)` without an assumption relating m and n). Big-O is an upper bound; interview tables usually give tight typical bounds.

---

## 2. How to calculate complexity for any solution

### 2.1 A reusable 9-step procedure

1. **Define input sizes.** For an array use `n`; for two arrays use `n,m`; for a graph use `V,E`; for a grid use `R,C`. State assumptions (sorted? balanced? hashing? fixed-width numbers?).
2. **Choose the case.** Normally report worst-case time, then mention average/amortized where meaningful.
3. **Identify the elementary operation.** A primitive array read is O(1), but `list.remove(0)` is O(n), `heap.offer()` is O(log n), and string copying costs its length.
4. **Analyze each loop separately.** Count *total iterations*, not just the number of nested `for`/`while` statements.
5. **Analyze recursion.** Determine the recurrence, recursion depth, and any work or memory allocated per call.
6. **Include library calls.** Sorting, copying, hashing a long string, resizing, and `substring()` can dominate an otherwise simple loop.
7. **Add sequential stages; multiply nested independent work.** Example: sort O(n log n) then scan O(n) → O(n log n). Nested loops of n and m → O(nm).
8. **Compute peak extra memory.** Count maps, arrays, queues, temporary copies, and recursion stack. Distinguish temporary space from the required output.
9. **Simplify and explain the reason.** Give a one- or two-sentence justification, not only a formula.

### 2.2 Fast rules for loops

| Code shape | Iteration count | Time |
|---|---:|---:|
| `for (i=0; i<n; i++)` | n | O(n) |
| Two separate n-loops | n+n | O(n) |
| Nested n-loops | n×n | O(n²) |
| Nested n-loop and m-loop | n×m | O(nm) |
| `for (j=0; j<i; j++)` nested under i | 0+1+...+(n−1) | O(n²) |
| `i *= 2` | ~log₂n | O(log n) |
| `i /= 2` | ~log₂n | O(log n) |
| Outer n, inner doubles to n | n log n | O(n log n) |
| Two monotonic pointers each advance ≤n times | ≤2n | O(n) |
| For each of n values, heap operation | n log n | O(n log n) |

**Crucial:** A nested `while` is *not automatically* O(n²). If `left` and `right` only move forward, each moves at most n times overall.

### 2.3 Recursion and recurrence patterns

| Recurrence | Typical time | Reason |
|---|---|---|
| `T(n)=T(n−1)+O(1)` | O(n) | n levels |
| `T(n)=T(n−1)+O(n)` | O(n²) | n+(n−1)+...+1 |
| `T(n)=T(n/2)+O(1)` | O(log n) | Halving |
| `T(n)=T(n/2)+O(n)` | O(n) | Geometric series n+n/2+... |
| `T(n)=2T(n/2)+O(n)` | O(n log n) | Merge sort |
| `T(n)=2T(n−1)+O(1)` | O(2^n) | Branching recursion |
| `T(n)=T(n−1)+T(n−2)+O(1)` | O(φ^n) | Naive Fibonacci; O(2^n) valid upper bound |

For recursive **space**, use **maximum simultaneous call depth**, not total number of calls. A depth-n chain of O(1)-space calls uses O(n) stack. Balanced binary-tree recursion uses O(log n) stack; skewed trees can use O(n). If each active frame allocates a buffer, count all buffers live simultaneously.

### 2.4 How to calculate auxiliary vs output space

| Solution | Auxiliary space | Output space |
|---|---|---|
| Reverse array in place | O(1) | O(1) |
| Copy an array | O(n) if copy is counted as workspace | O(n) if copy is required result |
| Two Sum with HashMap | O(n) | O(1) result pair |
| Recursive DFS on tree | O(h) stack | O(1) if only calculating a scalar |
| BFS on graph | O(V) visited + queue | Depends on requested result |
| Generate all subsets | O(n) recursion excluding output | O(n2^n) |
| Merge two sorted arrays into a new result | O(1) beyond result | O(n+m) |

When a question simply says “space complexity,” state your convention: “O(n) auxiliary space, excluding the output,” or “O(n) total additional space including the result.”

### 2.5 A template to say aloud in an interview

> Let n be the input size. I process each element [once / log n times / for every other element]. Each iteration performs [O(1) / O(log n)] work, so the total time is O(...). I allocate [data structure and size] and the recursion reaches at most [depth], so the auxiliary space is O(...). This is [worst-case / expected / amortized] because [...].

### 2.6 Decision flow for a new problem

```text
START
  |
  +-- Identify input dimensions (n, m, V, E, R, C)
  |
  +-- Are there loops?
  |     +-- Sequential? Add costs.
  |     +-- Nested independent? Multiply costs.
  |     +-- Pointers never reset? Count total movements.
  |     +-- Input halves/doubles? Usually log n.
  |
  +-- Is there recursion?
  |     +-- Write T(n), solve it, measure maximum stack depth.
  |
  +-- Are there library operations?
  |     +-- Look up costs of sort, heap, map, copying, list insertion.
  |
  +-- What memory is live at the same time?
  |     +-- Arrays/maps + queues/stacks + recursion + temporary buffers.
  |
  +-- Exclude/include output explicitly; simplify dominant terms.
  |
  +-- State assumptions and worst/average/amortized case.
END
```

---

## 3. Worked complexity analyses

### Example A — Single scan: O(n) time, O(1) space

```java
int max = Integer.MIN_VALUE;
for (int x : nums) {
    max = Math.max(max, x);
}
```

There are n iterations, O(1) work per iteration, and one scalar variable. **Time O(n), auxiliary space O(1)**.

### Example B — Nested loops: O(n²)

```java
for (int i = 0; i < n; i++) {
    for (int j = i + 1; j < n; j++) {
        check(i, j); // O(1)
    }
}
```

Work = (n−1)+(n−2)+...+1 = n(n−1)/2. **Time O(n²), space O(1)**.

### Example C — Sliding window: nested while, but O(n)

```java
int left = 0, sum = 0;
for (int right = 0; right < nums.length; right++) {
    sum += nums[right];
    while (sum > target) {
        sum -= nums[left++];
    }
}
```

Assuming nonnegative `nums`, right advances n times and left advances at most n times in total. **Time O(n), space O(1)**. The usual sum-based shrink rule is not generally correct with arbitrary negative numbers.

### Example D — Sorting + scanning: O(n log n)

```java
Arrays.sort(nums);                   // O(n log n)
for (int x : nums) process(x);      // O(n), if process is O(1)
```

Total O(n log n + n) = **O(n log n)**. Space depends on the Java array type and sorting implementation; do not blindly say O(1) for all `Arrays.sort` overloads.

### Example E — HashMap Two Sum: expected O(n)

```java
Map<Integer, Integer> seen = new HashMap<>();
for (int i = 0; i < nums.length; i++) {
    int need = target - nums[i];
    if (seen.containsKey(need)) return new int[] {seen.get(need), i};
    seen.put(nums[i], i);
}
return new int[0];
```

n loop iterations, expected O(1) hash operations. **Expected time O(n), auxiliary space O(n)**. Pathological collisions can worsen time.

### Example F — Binary search: O(log n)

```java
int left = 0, right = nums.length - 1;
while (left <= right) {
    int mid = left + (right - left) / 2;
    if (nums[mid] == target) return mid;
    if (nums[mid] < target) left = mid + 1;
    else right = mid - 1;
}
return -1;
```

Each iteration halves the search range. **Time O(log n), auxiliary space O(1)**.

### Example G — BFS graph: O(V+E)

```java
Queue<Integer> q = new ArrayDeque<>();
boolean[] visited = new boolean[graph.size()];
q.offer(source);
visited[source] = true;
while (!q.isEmpty()) {
    int u = q.poll();
    for (int v : graph.get(u)) {
        if (!visited[v]) {
            visited[v] = true;
            q.offer(v);
        }
    }
}
```

Each vertex enters the queue at most once; each adjacency entry is examined once. **Time O(V+E), auxiliary space O(V)**, excluding graph storage.

### Example H — Heap operations: O(n log k)

Maintain the largest k numbers using a min-heap of size k. For each of n numbers, a possible insert/poll costs O(log k). **Time O(n log k), auxiliary space O(k)**. If k=1, log k should be interpreted with a constant floor; the algorithm is O(n).

### Example I — Merge two sorted arrays: O(n+m)

```java
int[] merge(int[] a, int[] b) {
    int[] result = new int[a.length + b.length];
    int i = 0, j = 0, p = 0;
    while (i < a.length && j < b.length) {
        result[p++] = a[i] <= b[j] ? a[i++] : b[j++];
    }
    while (i < a.length) result[p++] = a[i++];
    while (j < b.length) result[p++] = b[j++];
    return result;
}
```

Each item is copied once. **Time O(n+m), output space O(n+m), O(1) auxiliary space beyond output**.

### Example J — Backtracking subsets: output changes the answer

There are 2^n subsets; copying a subset may take O(n). **Time O(n2^n), recursion auxiliary space O(n), output storage O(n2^n)**.

### Example K — Naive recursive Fibonacci

`fib(n)` makes overlapping recursive calls. Time **O(φ^n)** (often simplified to O(2^n) upper bound), stack **O(n)**. Memoization reduces time to O(n) and uses O(n) cache plus O(n) stack; bottom-up two-variable DP uses O(n) time and O(1) auxiliary space.

### Example L — Amortized ArrayList append

Most `add(x)` operations write to available capacity in O(1). Rare growth operations copy O(n) elements. Over a long sequence of appends with geometric growth, total copying is O(n), so append is **O(1) amortized**, but a specific append may be O(n).

---

## 4. Java data structures: operation-by-operation

### 4.1 Master table

Typical time unless otherwise marked; space is storage for n elements.

| Structure | Indexed read | Search | Insert | Delete | Storage |
|---|---|---|---|---|---|
| Array | O(1) | O(n) unsorted | O(n) shifting | O(n) shifting | O(n) |
| ArrayList | O(1) | O(n) | O(1) amortized append; O(n) middle | O(1) last; O(n) middle | O(n) |
| LinkedList | O(n) | O(n) | O(1) at ends/known node | O(1) at ends/known node | O(n) |
| ArrayDeque | O(1) at ends | O(n) | O(1) amortized ends | O(1) ends | O(n) |
| Stack via ArrayDeque | O(1) top | O(n) | O(1) amortized push | O(1) pop | O(n) |
| PriorityQueue | O(1) peek root | O(n) arbitrary | O(log n) | O(log n) poll root | O(n) |
| HashMap | O(1) expected by key | O(1) expected | O(1) amortized expected | O(1) expected | O(n) |
| HashSet | — | O(1) expected | O(1) amortized expected | O(1) expected | O(n) |
| TreeMap | O(log n) by key | O(log n) | O(log n) | O(log n) | O(n) |
| TreeSet | — | O(log n) | O(log n) | O(log n) | O(n) |
| Balanced BST | O(log n) by key | O(log n) | O(log n) | O(log n) | O(n) |
| Unbalanced BST | O(n) worst | O(n) worst | O(n) worst | O(n) worst | O(n) |
| Trie | O(L) by key | O(L) | O(L) | O(L) | O(total characters) |

### 4.2 Arrays

| Operation | Time | Explanation |
|---|---|---|
| `arr[i]` read / write | O(1) | Direct address calculation |
| Traverse | O(n) | Visit all elements |
| Search unsorted | O(n) | Scan |
| Binary search sorted | O(log n) | Halve search range |
| Insert beginning/middle in preallocated logical array | O(n) | Shift elements |
| Insert end with spare capacity | O(1) | Write at next slot |
| Delete beginning/middle | O(n) | Shift elements |
| Delete logical last | O(1) | Decrement size |
| Reverse in place | O(n) | Swap pairs |
| Copy | O(n) | Copy each element |
| Merge two arrays | O(n+m) | Copy or two-pointer merge |
| Sort `int[]` | O(n log n) worst in modern OpenJDK | Primitive dual-pivot quicksort family |

Java arrays have fixed physical length. “Insert” and “delete” imply managing a logical size, shifting, or allocating a replacement array.

### 4.3 ArrayList

| Operation | Time |
|---|---|
| `get(i)` / `set(i,x)` | O(1) |
| `add(x)` | O(1) amortized; O(n) on resize |
| `add(i,x)` | O(n) |
| `remove(last index)` | O(1) |
| `remove(i)` | O(n) |
| `contains(x)` / `indexOf(x)` | O(n) |
| `clear()` | O(n) |
| `addAll(m elements)` at end | O(m) amortized, assuming sufficient growth strategy |
| `toArray()` | O(n) |
| Object `sort()` | O(n log n) worst |

Example: `list.add(0, 5)` shifts existing elements; `list.add(5)` normally appends. `remove(Integer.valueOf(5))` searches by value, unlike `remove(5)` on `List<Integer>` which removes by index.

### 4.4 LinkedList

| Operation | Time |
|---|---|
| `get(i)` | O(n) |
| `addFirst` / `addLast` | O(1) |
| `removeFirst` / `removeLast` | O(1) |
| `contains` / `remove(Object)` | O(n) |
| Insert/delete with node reference and links | O(1) |
| Find middle, reverse, cycle detection | O(n) |
| Merge sorted lists | O(n+m) |

A known *position* is not the same as a known *node*. Finding index i can take O(n). Java `LinkedList` is doubly linked; a custom singly linked list has different deletion constraints.

### 4.5 Stack, Queue, Deque

| Operation | ArrayDeque implementation |
|---|---|
| `push` / `offerFirst` / `offerLast` | O(1) amortized |
| `pop` / `pollFirst` / `pollLast` | O(1) |
| `peek` / `peekFirst` / `peekLast` | O(1) |
| Search arbitrary element | O(n) |
| Iterate | O(n) |
| Storage | O(n) |

```java
Deque<Integer> stack = new ArrayDeque<>();
stack.push(10); stack.push(20); stack.pop();
Queue<Integer> queue = new ArrayDeque<>();
queue.offer(10); queue.offer(20); queue.poll();
```

### 4.6 HashMap / HashSet

| Operation | Expected | Worst possible |
|---|---|---|
| `get`, `containsKey`, `remove` | O(1) | O(n) |
| `put`, `HashSet.add` | O(1) amortized | O(n) |
| Iterate HashMap | O(n + capacity) | O(n + capacity) |
| Merge n-entry and m-entry hash maps | O(n+m) expected | Collision-dependent |

Java 8+ HashMap can treeify large collision bins when conditions permit, often improving a treeified-bin operation to O(log n), but this is not a universal guarantee for all keys or all operations. Hashing a string key may itself take O(L) if its hash is not cached. HashMap does not maintain sorted order.

### 4.7 TreeMap / TreeSet / BST

| Operation | Balanced BST / Java TreeMap |
|---|---|
| `get`, `put`, `remove` | O(log n) |
| `firstKey`, `lastKey`, `floorKey`, `ceilingKey` | O(log n) bound |
| Range iteration yielding k items | O(log n+k) |
| Sorted full traversal | O(n) |
| Repeatedly insert m keys | O(m log(n+m)) |
| Storage | O(n) |

`TreeMap` uses a red-black tree. A plain unbalanced BST has O(h) operations, where h can be n.

### 4.8 Heap / PriorityQueue

| Operation | Time |
|---|---|
| `peek` | O(1) |
| `offer` | O(log n) |
| `poll` | O(log n) |
| `contains` | O(n) |
| `remove(Object)` | O(n) search + O(log n) repair = O(n) |
| Bottom-up heapify n items | O(n) |
| Repeated insertion of n items | O(n log n) |
| Extract all sorted | O(n log n) |
| Top-k of n elements | O(n log k) |
| Merge heaps by rebuilding from all elements | O(n+m) |

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(30); pq.offer(10); pq.offer(20);
int smallest = pq.peek(); // 10
int removed = pq.poll();  // 10
```

---

## 5. Sorting and searching

### 5.1 Sorting

| Algorithm | Best | Average | Worst | Auxiliary space | Stable? |
|---|---|---|---|---|---|
| Optimized bubble | O(n) | O(n²) | O(n²) | O(1) | Yes |
| Selection | O(n²) | O(n²) | O(n²) | O(1) | No |
| Insertion | O(n) | O(n²) | O(n²) | O(1) | Yes |
| Merge | O(n log n) | O(n log n) | O(n log n) | O(n) | Yes |
| Standard quicksort | O(n log n) | O(n log n) | O(n²) | O(log n) avg stack; O(n) worst | No |
| Heap | O(n log n) | O(n log n) | O(n log n) | O(1) | No |
| Counting | O(n+k) | O(n+k) | O(n+k) | O(n+k) typical | If implemented stably |
| Radix | O(d(n+k)) | O(d(n+k)) | O(d(n+k)) | O(n+k) | With stable passes |
| Bucket | Distribution-dependent | O(n+k) expected under assumptions | O(n²) | O(n+k) typical | Depends |
| TimSort | O(n) | O(n log n) | O(n log n) | Up to O(n) | Yes |

`k` is value range/bucket count, `d` number of digit passes. Merge sort: `T(n)=2T(n/2)+O(n)=O(n log n)`. Quicksort worst case: `T(n)=T(n−1)+O(n)=O(n²)` for extremely unbalanced pivots.

**Java note:** Modern OpenJDK `Arrays.sort(int[])` uses a dual-pivot quicksort family with O(n log n) worst-case safeguards; `Arrays.sort(Object[])` and `List.sort` use stable adaptive merge-based sorting (TimSort) with O(n log n) worst-case time. Exact implementation and scratch memory vary by Java version and overload. Do not assume object sorting is in-place.

### 5.2 Searching

| Algorithm | Requirement | Time | Auxiliary space |
|---|---|---|---|
| Linear | None | O(n) | O(1) |
| Binary iterative | Sorted random-access sequence | O(log n) | O(1) |
| Binary recursive | Sorted random-access sequence | O(log n) | O(log n) |
| Jump | Sorted array | O(√n) | O(1) |
| Exponential | Sorted array | O(log(p+1)) | O(1) |
| Interpolation | Sorted numeric data | O(log log n) expected for uniform data; O(n) worst | O(1) |
| Hash lookup | Good hashing | O(1) expected | O(1) per lookup |
| Balanced BST | Balanced tree | O(log n) | O(1) iterative |

`p` is target index. Binary search on a linked list does not yield the same O(log n) random-access cost.

---

## 6. Trees and graphs

### 6.1 Binary trees and BST

| Operation / algorithm | Time | Auxiliary space |
|---|---|---|
| Inorder/preorder/postorder traversal | O(n) | O(h) recursion |
| Level-order traversal | O(n) | O(w), max width |
| Max depth | O(n) | O(h) |
| Diameter | O(n) optimized | O(h) |
| Validate BST | O(n) | O(h) |
| General BT lowest common ancestor | O(n) | O(h) |
| BST lowest common ancestor | O(h) | O(1) iterative |
| BST search/insert/delete | O(h) | O(1) iterative |
| Serialize tree | O(n) | O(n) output |
| Build tree using index map and traversals | O(n) expected | O(n) |

Balanced h=O(log n); skewed h=O(n). For recursive DFS, the call stack uses O(h), not always O(n).

### 6.2 Graph representations

| Operation | Adjacency list | Adjacency matrix |
|---|---|---|
| Storage | O(V+E) | O(V²) |
| Add edge | O(1) amortized | O(1) |
| Test edge | O(degree) with list | O(1) |
| Remove edge | O(degree) | O(1) |
| Enumerate neighbors | O(degree) | O(V) |
| BFS/DFS | O(V+E) | O(V²) |

A hash-set adjacency representation can make edge tests O(1) expected at extra memory cost.

### 6.3 Graph algorithms

| Algorithm | Time | Auxiliary space | Main use |
|---|---|---|---|
| BFS | O(V+E) | O(V) | Unweighted shortest paths |
| DFS | O(V+E) | O(V) | Traversal/connectivity |
| Kahn topological sort | O(V+E) | O(V) | DAG ordering |
| DFS topological sort | O(V+E) | O(V) | DAG ordering |
| Dijkstra + binary heap | O((V+E) log V) typical | O(V+E) with duplicate heap entries | Nonnegative shortest paths |
| Bellman–Ford | O(VE) | O(V) | Negative-weight edges |
| Floyd–Warshall | O(V³) | O(V²) | All-pairs shortest paths |
| Kruskal | O(E log E) | O(V+E) including sorted edges | MST |
| Prim + binary heap | O(E log V) typical | O(V+E) | MST |
| DSU with compression + rank | O(α(V)) amortized per operation | O(V) | Connectivity |
| Tarjan SCC | O(V+E) | O(V) | Strongly connected components |
| Kosaraju SCC | O(V+E) | O(V+E) including reverse graph | SCC |
| Bridges/articulation points | O(V+E) | O(V) | Critical connections |
| Bipartite check | O(V+E) | O(V) | Two-coloring |
| DAG shortest path | O(V+E) | O(V) | Weighted DAG paths |

`α(V)` is inverse Ackermann and grows extraordinarily slowly. Complexity assumes adjacency lists unless stated otherwise. Dijkstra's heap details vary: a decrease-key heap can use O(V) heap entries, while Java PriorityQueue implementations often insert duplicate candidates and may use O(E) heap entries.

---

## 7. Core coding patterns

| Pattern | Typical time | Auxiliary space | Representative problem |
|---|---|---|---|
| Two pointers | O(n) | O(1) | Two Sum II |
| Sliding window | O(n) | O(1)–O(k) | Longest substring |
| Fast/slow pointers | O(n) | O(1) | Cycle detection |
| Prefix sum build | O(n) | O(n) | Range sum |
| Prefix sum query | O(1) | O(1) | Range sum after build |
| Prefix sum + HashMap | O(n) expected | O(n) | Subarray sum equals K |
| Difference array | O(n+q) | O(n) | Range increment queries |
| Binary search | O(log n) | O(1) iterative | Search insert position |
| Binary search on answer | O(log R × check(n)) | Depends | Minimum capacity |
| Monotonic stack | O(n) | O(n) | Daily temperatures |
| Monotonic deque | O(n) | O(k) | Sliding window maximum |
| Heap top-k | O(n log k) | O(k) | Kth largest |
| Merge sorted arrays | O(n+m) | O(1) excluding result | Merge two arrays |
| Merge intervals | O(n log n) | O(n) output typical | Merge intervals |
| Sweep line | O(n log n) | O(n) | Meeting rooms |
| Divide and conquer | Often O(n log n) | Depends | Merge sort |
| Kadane | O(n) | O(1) | Maximum subarray |
| Boyer–Moore voting | O(n) | O(1) | Majority element |
| Greedy | O(n) or O(n log n) typical | Depends | Interval scheduling |
| Reservoir sampling | O(n) | O(k) | Sample k values |
| Meet-in-the-middle | O(2^(n/2)) typical | O(2^(n/2)) | Subset sum |

**Pattern reasoning:** Two pointers often do O(n) total moves. Monotonic stacks are O(n) because each element is pushed and popped at most once. Prefix sums trade O(n) preprocessing/storage for O(1) immutable range queries. Binary search on an answer costs O(log R) feasibility checks, each of which must be analyzed separately.

---

## 8. Dynamic programming and backtracking

### 8.1 DP: states × transitions

**Time = number of states × work per state.** Space is the number of stored states plus recursion stack, if any. If rolling arrays are used, only live rows/columns need to be counted.

| Problem | Time | Auxiliary space |
|---|---|---|
| Fibonacci naive recursion | O(2^n) upper bound | O(n) |
| Fibonacci memoized | O(n) | O(n) |
| Fibonacci iterative | O(n) | O(1) |
| Climbing stairs optimized | O(n) | O(1) |
| House robber optimized | O(n) | O(1) |
| 0/1 knapsack | O(nW) | O(W) optimized |
| Coin change minimum coins | O(nA) | O(A) |
| LIS O(n²) DP | O(n²) | O(n) |
| LIS binary search | O(n log n) | O(n) |
| Longest common subsequence | O(nm) | O(nm), O(min(n,m)) optimized |
| Edit distance | O(nm) | O(nm), O(min(n,m)) optimized |
| Matrix-chain multiplication | O(n³) | O(n²) |
| Interval DP | Often O(n³) | Often O(n²) |
| Bitmask DP | Often O(n2^n) | O(2^n) or O(n2^n) |

### 8.2 Backtracking

| Search | Number of possibilities | Time to materialize all | Auxiliary stack/state | Output storage |
|---|---|---|---|---|
| Subsets | 2^n | O(n2^n) | O(n) | O(n2^n) |
| Permutations | n! | O(n·n!) | O(n) | O(n·n!) |
| Binary decisions depth n | Up to 2^n leaves | O(2^n) plus work at leaves | O(n) | Depends |

Pruning can improve actual runtime without changing the worst-case exponential bound.

---

## 9. Strings, matrices, bits, and math

### 9.1 Java strings

| Operation | Time | Auxiliary space |
|---|---|---|
| `String.length()` / `charAt(i)` | O(1) | O(1) |
| `String.equals()` | O(n) worst | O(1) |
| `substring(a,b)` modern Java | O(k) | O(k) |
| `a + b` string concatenation | O(n+m) | O(n+m) |
| `StringBuilder.append(char)` | O(1) amortized | O(1) amortized growth per char |
| `StringBuilder.insert(i,str)` | O(n+m) | Depends on resize |
| `StringBuilder.reverse()` | O(n) | O(1) typical |
| Naive substring search | O(nm) | O(1) |
| KMP | O(n+m) | O(m) |
| Rabin–Karp | O(n+m) expected with suitable hashing; O(nm) worst | O(1) fixed-size rolling hash |
| Z algorithm | O(n) | O(n) |
| Manacher | O(n) | O(n) |
| Trie prefix lookup | O(L) | O(1) auxiliary per query |

Repeated `s = s + ch` in a loop can copy progressively longer strings, giving O(n²) total time. Prefer `StringBuilder`. Java `String` length counts UTF-16 code units, not Unicode grapheme clusters.

### 9.2 Matrices/grids

| Operation | Time | Auxiliary space |
|---|---|---|
| Traverse R×C | O(RC) | O(1) |
| Grid BFS/DFS | O(RC) | O(RC) worst |
| Flood fill | O(RC) | O(RC) worst |
| Rotate n×n in place | O(n²) | O(1) |
| Spiral traversal | O(RC) | O(1) excluding output |
| 2D prefix sum build | O(RC) | O(RC) |
| 2D prefix sum query | O(1) | O(1) |
| Naive n×n matrix multiplication | O(n³) | O(n²) output |
| Binary search row-major sorted matrix | O(log(RC)) | O(1) |

### 9.3 Bit manipulation

| Operation | Time | Auxiliary space |
|---|---|---|
| Check/set/clear/toggle bit in `int` | O(1) | O(1) |
| `Integer.bitCount(x)` | O(1) for fixed 32 bits | O(1) |
| XOR n elements | O(n) | O(1) |
| Single number using XOR | O(n) | O(1) |
| Enumerate all n-bit subsets | O(2^n) masks; O(n2^n) if processing every element | O(1)–O(n) |

### 9.4 Number theory

| Algorithm | Time | Auxiliary space |
|---|---|---|
| Euclidean GCD | O(log min(a,b)) | O(1) iterative |
| Fast exponentiation | O(log p) | O(1) iterative |
| Trial-division primality | O(√n) | O(1) |
| Sieve of Eratosthenes | O(n log log n) | O(n) |
| Modular exponentiation | O(log p) | O(1) |
| Iterative factorial | O(n) | O(1) for fixed-width integers |

For arbitrary-precision `BigInteger`, arithmetic is not O(1): include operand bit lengths.

---

## 10. Advanced data structures

| Structure | Build | Query | Update | Storage |
|---|---|---|---|---|
| Fenwick tree / BIT | O(n) optimized | O(log n) prefix sum | O(log n) point add | O(n) |
| Segment tree | O(n) | O(log n) range aggregate | O(log n) point update | O(n) |
| Lazy segment tree | O(n) | O(log n) | O(log n) range update | O(n) |
| Sparse table | O(n log n) | O(1) for idempotent aggregates | Rebuild | O(n log n) |
| Trie | O(total characters) | O(L) | O(L) | O(total characters) |
| Binary trie | O(nB) | O(B) | O(B) | O(nB) worst |
| LRU cache (HashMap + doubly linked list) | O(capacity) | O(1) expected | O(1) expected | O(capacity) |
| LFU cache (maps + frequency lists) | O(capacity) | O(1) expected | O(1) expected | O(capacity) |
| Bloom filter | O(1) per insertion for fixed k | O(k) | O(k) insert | O(m) bits |
| Skip list | O(n log n) expected repeated build | O(log n) expected | O(log n) expected | O(n) expected |

`B` = key bits, `L` = key length, `m` = Bloom-filter bit count, `k` = number of hashes. Standard Bloom filters do not safely support exact deletion. Use Fenwick for prefix sums/point updates; segment trees for more general range aggregates and lazy range updates.

---

## 11. Java-specific complexity traps

1. **`ArrayList.remove(0)` is O(n)** because elements shift. Repeating it n times is O(n²).
2. **`PriorityQueue.remove(Object)` is O(n)**; `poll()` is O(log n).
3. **`LinkedList.get(i)` is O(n)**; linked lists do not support constant-time random access.
4. **`HashMap` O(1) is expected/amortized**, not a universal worst-case guarantee.
5. **`TreeMap` is O(log n)**, useful when sorted keys, floor/ceiling, or range queries are required.
6. **`Arrays.sort(int[])` and `Arrays.sort(Object[])` have different algorithms and memory behavior.**
7. **`String.substring` copies characters in modern Java**: O(k) time and storage.
8. **`StringBuilder` append is amortized O(1)** for a single character; appending a length-m string is O(m).
9. **Hashing strings can cost O(L)** on first hash computation; do not automatically treat every hash-key operation as independent of key length.
10. **`Collections.binarySearch` on `LinkedList` may cost O(n)** because indexed access is expensive.
11. **Recursive DFS may overflow the Java call stack** on very deep trees/graphs; iterative DFS uses an explicit stack.
12. **Primitive arrays vs boxed collections:** `int[]` has lower memory overhead than `ArrayList<Integer>`; Big-O does not capture object allocation, boxing, cache locality, or GC pressure.
13. **Java 8 `Map.computeIfAbsent`** still has expected hash lookup costs plus the mapping function's own complexity; include that function.
14. **Iterating a HashMap** may cost O(capacity + size), not strictly O(size), especially with excess capacity.
15. **`List<Integer>.remove(1)` removes index 1**, whereas `remove(Integer.valueOf(1))` removes the value 1.

---

## 12. Interview cheat sheets and practice

### 12.1 Quick operations reference

| Goal | Strong default | Cost |
|---|---|---|
| Indexed read/write | Array / ArrayList | O(1) |
| Append elements | ArrayList | O(1) amortized |
| Push/pop both ends | ArrayDeque | O(1) amortized insertion, O(1) removal |
| Membership check | HashSet | O(1) expected |
| Key→value lookup | HashMap | O(1) expected |
| Sorted keys and floor/ceiling | TreeMap / TreeSet | O(log n) |
| Repeated min/max removal | PriorityQueue | O(log n) poll |
| Find top k | Heap of size k | O(n log k) |
| Static sorted-array lookup | Binary search | O(log n) |
| Unweighted shortest path | BFS | O(V+E) |
| Nonnegative weighted shortest path | Dijkstra | O((V+E) log V) typical |
| DAG ordering | Kahn / DFS topo | O(V+E) |
| Connectivity unions | DSU | O(α(V)) amortized |
| Immutable range sums | Prefix sums | O(n) build, O(1) query |
| Dynamic prefix sums | Fenwick tree | O(log n) update/query |
| General dynamic range aggregates | Segment tree | O(log n) update/query |

### 12.2 Questions interviewers ask

**Q: Why can nested loops be O(n)?** Because each pointer may move at most n times total across the entire run, even when movement happens in an inner loop.

**Q: Why is building a heap O(n), not O(n log n)?** Bottom-up heapify operates on many nodes near the leaves (cheap sift-downs); summing work by node height gives O(n). Repeated insertion is O(n log n).

**Q: Why is BFS O(V+E)?** Each vertex is enqueued at most once and each adjacency entry is inspected a bounded number of times.

**Q: Why is binary search O(log n)?** After t steps, the remaining search space is at most n/2^t; it becomes 1 after t≈log₂n steps.

**Q: Why can recursion use O(n) space without allocating arrays?** Each active recursive call occupies a stack frame; depth n means n simultaneously active frames.

**Q: Why can sorting + a linear pass be O(n log n)?** Sequential costs add: n log n + n, dominated by n log n.

**Q: What if I allocate an O(n) result array?** Say whether you count output space. Example: “O(1) auxiliary beyond the O(n) output array.”

### 12.3 Mini practice — determine time and auxiliary space

1. Scan an array and store each value in a HashSet. **Answer:** expected O(n) time, O(n) space.
2. For each array element, binary search a sorted array of size m. **Answer:** O(n log m) time, O(1) iterative space.
3. Repeatedly remove index 0 from an ArrayList of n items. **Answer:** O(n²) time, O(1) auxiliary space.
4. Repeatedly poll n elements from a heap. **Answer:** O(n log n) time, O(1) extra beyond the heap (excluding results).
5. Recursive inorder traversal of a balanced tree. **Answer:** O(n) time, O(log n) stack.
6. Recursive inorder traversal of a skewed tree. **Answer:** O(n) time, O(n) stack.
7. Merge two sorted arrays into a new array. **Answer:** O(n+m) time, O(n+m) output space, O(1) beyond output.
8. Enumerate all permutations and copy each into a result list. **Answer:** O(n·n!) time, O(n) working state, O(n·n!) output.
9. Process n values with a heap of size at most k. **Answer:** O(n log k) time, O(k) space.
10. Use 2D DP with n×m states and O(1) transitions. **Answer:** O(nm) time, O(nm) table space, potentially O(min(n,m)) with rolling rows when dependencies allow.

### 12.4 Final checklist before answering

- [ ] Define every variable (n, m, V, E, h, k).
- [ ] Identify worst, average/expected, and amortized cases when needed.
- [ ] Count total loop iterations, not simply loop nesting.
- [ ] Include library operation costs inside loops.
- [ ] Solve recursion recurrence and count maximum active stack frames.
- [ ] Add sequential stages; multiply truly nested work.
- [ ] Count live data structures, copies, and temporary buffers.
- [ ] State whether required output is excluded from auxiliary space.
- [ ] Simplify to the dominant growth term without dropping independent variables.
- [ ] Explain **why** the bound holds in plain language.

---

**Reference note:** Complexity claims assume conventional implementations and comparison/hash costs unless otherwise specified. Exact Java runtime behavior can differ by JDK version, input distribution, comparator cost, and object representation.
