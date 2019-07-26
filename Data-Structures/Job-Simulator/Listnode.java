/**
 * This is the structure for a singly linked list node Consists of the data to
 * be stored in the node and the link to the next node in the list
 * 
 * @param <E>
 *            - Type of data the Node should contain
 */
public class Listnode<E> {

	/**
	 * Data Members: 
	 * 
	 * data: Holds the value of the data contained of the
	 * currently referenced node in the list
	 *  
	 * next: Holds the link to the next node in list
	 */
	private E data;
	private Listnode<E> next;

	/** Constructor that creates a node for a linked list 
	 * 
    * @param data - data of type E, the node will contain
    * 
    */
	public Listnode(E data) {
		this.data = data;
		this.next = null;
	}
	
	/** Constructor that creates a node for a linked list 
	 * 
    * @param data - data of type E, the node will contain
    * 
    * @param next - reference to the next node in the list
    * 
    */
	public Listnode(E data, Listnode<E> next) {
		this.data = data;
		this.next = next;
	}
	
	/** 
    * @return data - data stored in the node 
    */
	public E getData() {
		return data;
	}
	
	/** Updates the data held in the node  
	 * 
    * @param data - data of type E, the node will update to contain 
    * 
    */
	public void setData(E data) {
		this.data = data;
	}
	
	/** 
    * @return next - the reference of node it is linked to in the list 
    */
	public Listnode<E> getNext() {
		return next;
	}
	
	/** Sets what the node will link to   
	 * 
    * @param next - reference of node, the current node should link to 
    * 
    */
	public void setNext(Listnode<E> next) {
		this.next = next;
	}
}