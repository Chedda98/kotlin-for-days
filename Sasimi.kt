class Sasimi {

    data class User(val enable: Boolean, val name: String)
    //커링
    fun curryTest() {
        val users = listOf (User(false, "rama"), User(true, "sasimi"))
        userFor(users) { user ->
            //....
            print(user)
        }
    }

    private fun userFor(users: List<User>, f: (User) -> Unit) {
        for(i in users) {
            if (i.enable)
                f(i)
        }
    }

    //fold
    private fun <T, R> Collection<T>.fold(
        initial: R,
        combine: (acc: R, nextElement: T) -> R
    ): R {
        var accumulator: R = initial
        for (element: T in this) {
            accumulator = combine(accumulator, element)
        }
        return accumulator
    }

    fun foldTest() {
        val items = listOf(1, 2, 3, 4, 5)

        items.fold(0) { acc: Int, i: Int ->
            val result = acc + i
            result
        }

        val joinedToString = items.fold("Elements:") { acc, i -> "$acc, $i" }
        val product = items.fold(1, Int::times)
    }


    //if
    fun testIf(): Int {
        val a = 100
        val b = 200
        val max = if (a > b) {
            print("Choose a")
            a
        } else {
            print("Choose b")
            b
        }

        return 1
    }


    //when
    enum class Color {
        RED, GREEN, BLUE
    }

    fun testWhen() {
        val x = 2
        when (x) {
            1, 2, 3 -> print("x == 1")
            1..10 -> print("x == 2")
            else -> {
                print("x is neither 1 nor 2")
            }
        }
        when (getColor()) {
            Color.RED -> println("red")
            Color.GREEN -> println("green")
            Color.BLUE -> println("blue")
            // 'else' is not required because all cases are covered
        }

        when (getColor()) {
            Color.RED -> println("red") // no branches for GREEN and BLUE
            else -> println("not red") // 'else' is required
        }

        fun hasPrefix(x: Any) = when (x) {
            is String -> x.startsWith("prefix")
            else -> false
        }
    }

    fun testFor() {
        for (i in 1..3) {
            println(i)
        }
        for (i in 6 downTo 0 step 2) {
            println(i)
        }

        for ((index, value) in (1..10).withIndex()) {
            println("the element at $index is $value")
        }

        loop@ for (i in 1..100) {
            for (j in 1..100) {
                if (i==10 && j==20) break@loop
            }
        }

        fun testFor2() {
            run {
                listOf(1, 2, 3, 4, 5).forEach {
                    if (it == 3) return // 함수전체에 대한 return
                    print(it)
                }
                println("this point is unreachable")
            }


            run {
                listOf(1, 2, 3, 4, 5).forEach lit@{
                    if (it == 3) return@lit // 람다 함수 lit@에 대한 리턴
                    print(it)
                }
                print(" done with explicit label")
            }

            run {
                listOf(1, 2, 3, 4, 5).forEach {
                    if (it == 3) return@forEach // local return to the caller of the lambda - the forEach loop
                    print(it)
                }
                print(" done with implicit label")
            }

            run {
                fun foo() {
                    listOf(1, 2, 3, 4, 5).forEach(fun(value: Int) {
                        if (value == 3) return  // local return to the caller of the anonymous function - the forEach loop
                        print(value)
                    })
                    print(" done with anonymous function")
                }
            }
        }


        //함수타입
        val a: String? = null
        val repeatFun: String.(Int) -> String = { times -> this.repeat(times) }
        val twoParameters: (String, Int) -> String = repeatFun // OK

        fun runTransformation(f: (String, Int) -> String): String {
            return f("hello", 3)
        }
        val result = runTransformation(repeatFun) // OK


        //연산자 오버로딩
        //직접 개발한 오브젝트에 연산자를 오버로딩하여 가독성을 높일 수 있음

        //단일 연산자
        data class Point(val x: Int, val y: Int)
        operator fun Point.unaryMinus() = Point(-x, -y)

        fun unary() {
            val point = Point(10, 20)
            println(-point)  // prints "Point(x=-10, y=-20)"
        }

        operator fun Point.inc() = Point(x+1, y+1)
        fun inc() {
            var point = Point(10, 20)
            println(++point)  // prints "Point(x=11, y=21)"
        }

        /* 바이너리 연상자.
        Expression      Translated to
        a + b           a.plus(b)
        a - b           a.minus(b)
        a * b           a.times(b)
        a / b           a.div(b)
        a % b           a.rem(b)
        a..b            a.rangeTo(b)
         */
        data class Counter(val dayIndex: Int) {
            operator fun plus(increment: Int): Counter {
                return Counter(dayIndex + increment)
            }

            operator fun plus(increment: Counter): Counter {
                return Counter(dayIndex + increment.dayIndex)
            }
        }

        fun plusTest() {
            val counterInt = Counter(10) + 20
            val counterObj = Counter(10) + Counter(20)
        }

        /*  in 연산자
        Expression          Translated to
        a in b              b.contains(a)
        a !in b             !b.contains(a)
        */

        data class Company(val name: String) {
            operator fun contains(user: String): Boolean {
                return name == user
            }
        }

        fun inTest() {
            val company = Company("rama")
            println("sasimi" in company)
        }


        /*인덱스 연산자
        Expression              Translated to
        a[i]                    a.get(i)
        a[i, j]                 a.get(i, j)
        a[i_1, ..., i_n]        a.get(i_1, ..., i_n)
        a[i] = b                a.set(i, b)
        a[i, j] = b             a.set(i, j, b)
        a[i_1, ..., i_n] = b    a.set(i_1, ..., i_n, b)
        */

        data class Users(val users: List<User>) {
            operator fun get(i: Int): User {
                return users[i]
            }

            operator fun get(name: String): User? {
                return users.find { it.name == name }
            }
        }

        fun indexTest() {
            val users = Users(listOf(
                User(true,"rama"), User(true, "sasimi")
            ))

            println(users[1])
            println(users["sasimi"])
        }

        // ==
        class DateTime(
            /** The millis from 1970-01-01T00:00:00Z */
            private var millis: Long = 0L,
            private var timeZone: TimeZone? = null
        ) {
            private var asStringCache = ""
            private var changed = false

            override fun equals(other: Any?): Boolean =
                other is DateTime &&
                        other.millis == millis &&
                        other.timeZone == timeZone

            //...
        }


        data class DateTime(
            private var millis: Long = 0L,
            private var timeZone: TimeZone? = null
        ) {
            private var asStringCache = ""
            private var changed = false

            //...
        }

        /* 할당 연산자
        Expression          Translated to
        a += b              a.plusAssign(b)
        a -= b              a.minusAssign(b)
        a *= b              a.timesAssign(b)
        a /= b              a.divAssign(b)
        a %= b              a.remAssign(b)
        */


        //null 허용
        fun testNull{
            val name = "rama"
            var a: String = "abc" // Regular initialization means non-null by default
            a = null // compilation error

            var b: String? = "abc" // can be set to null
            b = null // ok
            print(b)
        }

        fun checkNull() {

            //자바라면
            val user: User? = null
            val name = if (user == null) "not defined" else user.name

            //좀더 복잡하면
            val lable: String? = "Kotlin"
            if (lable != null && lable.length > 0) {
                print("String of length ${lable.length}")
            } else {
                print("Empty string")
            }


            val a = "Kotlin"
            val bn: String? = null
            println(lable?.length)
            println(bn?.length) // Unnecessary safe call

            // elvis operator
            //자바라면
            val bb: String? = null
            val l: Int = if (bb != null) bb.length else -1

            //코틀린은
            val l = bb?.length ?: -1

            // !! 연산자
            val l = bb!!.length


            //scope function
            data class Person(var name: String, var age: Int)

            //전통적인 방식
            val person = Person("", 0)
            person.name = "James"
            person.age = 56
            println("$person")

            // Person(name=James, age=56)

            //let
            val resultIt = person.let {
                it.name = "James"
                it.age = 56
                it // (T)->R 부분에서의 R에 해당하는 반환값.
            }

            val resultStr = person.let {
                it.name = "Steve"
                it.age = 59
                "{${it.name} is ${it.age}" // (T)->R 부분에서의 R에 해당하는 반환값.
            }

            println("$resultIt")
            println("$resultStr")
        }

        //list, set, map......
        //constructing
        val numbersSet = setOf("one", "two", "three", "four")
        val emptySet = mutableSetOf<String>()

        val numbersMap = mapOf("key1" to 1, "key2" to 2, "key3" to 3, "key4" to 1)
        val numbersMap2 = mutableMapOf<String, String>().apply { this["one"] = "1"; this["two"] = "2" }

        //empty
        val empty = emptyList<String>()

        //이터레이터  Iterable<T>
        val numbers = listOf("one", "two", "three", "four")
        val numbersIterator = numbers.iterator()
        while (numbersIterator.hasNext()) {
            println(numbersIterator.next())
        }

        val numbers = listOf("one", "two", "three", "four")
        for (item in numbers) {
            println(item)
        }


        //리스트 이터레이터  ListIterator<T>
        val numbers = listOf("one", "two", "three", "four")
        val listIterator = numbers.listIterator()
        while (listIterator.hasNext()) listIterator.next()
        println("Iterating backwards:")
        while (listIterator.hasPrevious()) {
            print("Index: ${listIterator.previousIndex()}")
            println(", value: ${listIterator.previous()}")
        }

        // mutable 이터레이터   MutableIterator<T>
        val numbers = mutableListOf("one", "two", "three", "four")
        val mutableIterator = numbers.iterator()

        mutableIterator.next()
        mutableIterator.remove()
        println("After removal: $numbers")


        //range
        if (ii in 1..4) { // equivalent of i >= 1 && i <= 4
            print(ii)
        }

        for (i in 4 downTo 1) print(i)


        //sequence   eager evaluation vs lazy evaluation
        val col = (1..100000).toList().filter{it % 2 == 0}.map {it.toString()}.take(2)
        val seq = (1..100000).asSequence().filter{it % 2 == 0}.map {it.toString()}.take(2)
        //https://kotlinlang.org/docs/sequences.html#sequence-processing-example

        //sequence yield  -> filter/map 으로 표현하기 쉽지 않은경우
        val user = sequence {
            for (n in numbers) {
                //transform..
                //redis뒤지고..
                //다름 함수 들렸다온 결과
                yield(0)
            }
        }


        //transformation operation
        //map
        val numbers = setOf(1, 2, 3)
        println(numbers.map { it * 3 })
        println(numbers.mapIndexed { idx, value -> value * idx })

        val numbers = setOf(1, 2, 3)
        println(numbers.mapNotNull { if ( it == 2) null else it * 3 })
        println(numbers.mapIndexedNotNull { idx, value -> if (idx == 0) null else value * idx })

        //zip  -> Pair
        val colors = listOf("red", "brown", "grey")
        val animals = listOf("fox", "bear", "wolf")
        println(colors zip animals)
        val request = listOf(1,2,3,4)
        val response = getName() // ["a", "b", "c", "d"]
        val idAndName = request zip response

        val twoAnimals = listOf("fox", "bear")
        println(colors.zip(twoAnimals))

        //flatten
        val numberSets = listOf(setOf(1, 2, 3), setOf(4, 5, 6), setOf(1, 2))
        println(numberSets.flatten())


        //string 표현
        val numbers = listOf("one", "two", "three", "four")
        println(numbers)            //[one, two, three, four]
        println(numbers.joinToString()) //one, two, three, four

        val listString = StringBuffer("The list of numbers: ")
        numbers.joinTo(listString)
        println(listString)     //The list of numbers: one, two, three, four


        //filter
        val numbers = listOf("one", "two", "three", "four")
        val longerThan3 = numbers.filter { it.length > 3 }
        println(longerThan3)

        val numbersMap = mapOf("key1" to 1, "key2" to 2, "key3" to 3, "key11" to 11)
        val filteredMap = numbersMap.filter { (key, value) -> key.endsWith("1") && value > 10}
        println(filteredMap)

        //partition
        val numbers = listOf("one", "two", "three", "four")
        val (match, rest) = numbers.partition { it.length > 3 }

        println(match)
        println(rest)


        //연산자
        val numbers = listOf("one", "two", "three", "four")

        val plusList = numbers + "five"
        val minusList = numbers - listOf("three", "four")
        println(plusList)
        println(minusList)


        //그룹 연산자자
       val numbers = listOf("one", "two", "three", "four", "five")

        println(numbers.groupBy { it.first().uppercase() })     //{O=[one], T=[two, three], F=[four, five]}
        println(numbers.groupBy(keySelector = { it.first() }, valueTransform = { it.uppercase() })) //{o=[ONE], t=[TWO, THREE], f=[FOUR, FIVE]}


        //slice
        val numbers = listOf("one", "two", "three", "four", "five", "six")
        println(numbers.slice(1..3))        //[two, three, four]
        println(numbers.slice(0..4 step 2))     //[one, three, five]
        println(numbers.slice(setOf(3, 5, 0)))      //[four, six, one]


        //take & drop
        val numbers = listOf("one", "two", "three", "four", "five", "six")
        println(numbers.take(3))
        println(numbers.takeLast(3))
        println(numbers.drop(1))
        println(numbers.dropLast(5))

        //chunked
        val numbers = (0..13).toList()
        println(numbers.chunked(3)) //[[0,1,2], [3,4,5], [5,6,7], [8,9,10], [11, 12, 13]]
        println(numbers.chunked(3) { it.sum() })  // [3, 12, 21, 30, 35]


        //order 기본
        val numbers = listOf("one", "two", "three", "four")
        println("Sorted ascending: ${numbers.sorted()}")
        println("Sorted descending: ${numbers.sortedDescending()}")

        //order 커스텀
        val numbers = listOf("one", "two", "three", "four")

        val sortedNumbers = numbers.sortedBy { it.length }
        println("Sorted by length ascending: $sortedNumbers")
        val sortedByLast = numbers.sortedByDescending { it.last() }
        println("Sorted by the last letter descending: $sortedByLast")


        //Comparable
        class Version(val major: Int, val minor: Int): Comparable<Version> {
            override fun compareTo(other: Version): Int = when {
                this.major != other.major -> this.major compareTo other.major // compareTo() in the infix form
                this.minor != other.minor -> this.minor compareTo other.minor
                else -> 0
            }
        }

        //aggregate
        val numbers = listOf(6, 42, 10, 4)

        println("Count: ${numbers.count()}")
        println("Max: ${numbers.maxOrNull()}")
        println("Min: ${numbers.minOrNull()}")
        println("Average: ${numbers.average()}")
        println("Sum: ${numbers.sum()}")

        println(numbers.sumOf { it * 2 })
        println(numbers.sumOf { it.toDouble() / 2 })

        //fold reduce
        val numbers = listOf(5, 2, 10, 4)
        val simpleSum = numbers.reduce { sum, element -> sum + element } //초기값없이 첫번째 요소로
        println(simpleSum)
        val sumDoubled = numbers.fold(0) { sum, element -> sum + element * 2 } //초기값 지정
        println(sumDoubled)

        //mutable 컬렉션 수정
        //add
        val numbers = mutableListOf(1, 2, 3, 4)
        println(numbers)

        //addAll
        val numbers = mutableListOf(1, 2, 5, 6)
        numbers.addAll(arrayOf(7, 8))
        println(numbers)
        numbers.addAll(2, setOf(3, 4))
        println(numbers)

        //오퍼레이터 방식
        val numbers = mutableListOf("one", "two")
        numbers += "three"
        println(numbers)
        numbers += listOf("four", "five")
        println(numbers)

        //remove
        val numbers = mutableListOf(1, 2, 3, 4, 3)
        numbers.remove(3)                    // removes the first `3`
        println(numbers)
        numbers.remove(5)                    // removes nothing
        println(numbers)

        //오퍼레이터 방식
        val numbers = mutableListOf("one", "two", "three", "three", "four")
        numbers -= "three"
        println(numbers)        //[one, two, three, four]
        numbers -= listOf("four", "five")
        println(numbers)        //[one, two, three]


        //list전용 함수

        //set전용 함수

        //map 전용함수
    }

    private fun getColor(): Color {
        return Color.BLUE
    }
}
