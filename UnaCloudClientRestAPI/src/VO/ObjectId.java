package VO;

/**
 * Class that represents an object that has an id of the given class
 * @param <T> Given class of the id
 *  @author s.guzmanm
 */
public class ObjectId<T>
{
    //Id
    private T id;
    //Constructor
    public ObjectId(T id)
    {
        this.id=id;
    }

    //----------------
    //Getters and setters
    //--------------
    public T getId()
    {
        return id;
    }

    public void setId(T id)
    {
        this.id=id;
    }


}