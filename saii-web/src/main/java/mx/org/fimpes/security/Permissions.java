package mx.org.fimpes.security;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
public interface Permissions<T> {
    
    default boolean isAbleToCreate(){
        return false;
    }
    default boolean isAbleToEdit(){
        return false;
    }
    default boolean isAbleToEdit(T item){
        return false;
    }
    default boolean isAbleToRemove(){
        return false;
    }
    default boolean isAbleToRemove(T item){
        return false;
    }
    default boolean isAbleToSearch(){
        return false;
    }
    default boolean isAbleToSearch(T item){
        return false;
    }
    default boolean isAbleToPrint(){
        return false;
    }
    default boolean isAbleToPrint(T item){
        return false;
    }
}
