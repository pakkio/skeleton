def head[A](xs: List[A])= xs(0)

head(List(2,2,3))

case class Car(name:String)
head(List(Car("fiat"),Car("renault")))

//def plus[A](v1:A,V2:A) = ???

trait Plus[A] {
  def plus(a1:A,a2:A):A
}
def plus[A:Plus](a1:A,a2:A):A = implicitly[Plus[A]].plus(a1,a2)

trait PlusInt[Int]{
  def plus(a1:Int,a2:Int):Int = a1 + a2
}

plus(1,2)

