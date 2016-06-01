package chaitin.utils


class BoundedPriorityQueue[T<:Comparable[T]](heap: Array[T]) {

  val capacity = heap.length
  var size = 0

  def add(e: T): Unit = {
    if(size < capacity) {
      heap.update(size, e)
      size+=1
      bubbleUp()
    } else if(size > 0) {
      if(e.compareTo(heap(0)) > 0) {
        heap.update(0, e)
        bubbleDown(size)
      }
    }
  }

  def pluckMin(): T = {
    val min = heap(0)
    heap.update(0, heap(size-1))
    size-=1
    bubbleDown(size)
    min
  }

  def hasMore = size>0

  def bubbleUp(): Unit = {
    var i = size - 1
    while(i>0) {
      val parent_i = (i-1)>>1
      if(heap(i).compareTo(heap(parent_i)) < 0) {
        val temp = heap(parent_i)
        heap.update(parent_i, heap(i))
        heap.update(i, temp)
        i = parent_i
      }
      else
        i=0

    }
  }

  def bubbleDown(s: Int): Unit = {
    var i = 0
    var child_i = (i<<1)+1
    while(child_i<s) {
      val minChild =
        if(child_i+1 == s) child_i
        else if(heap(child_i).compareTo(heap(child_i+1)) < 0) child_i
        else child_i+1

      if(heap(i).compareTo(heap(minChild))>0) {
        val temp = heap(i)
        heap.update(i, heap(minChild))
        heap.update(minChild, temp)
        i = minChild
        child_i = (i<<1)+1
      }
      else
        child_i = s
    }
  }

  def sortAndFetch(offset: Int, limit: Int): Array[T] = {
    var s = size
    while(s>0) {
      val temp = heap(0)
      heap.update(0, heap(s-1))
      heap.update(s-1, temp)
      bubbleDown(s-1)
      s-=1
    }
    heap.slice(offset, math.min(offset+limit, size))
  }

}
