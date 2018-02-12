package uniandes.unacloud.web.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includeFields = true, excludes = 'dateCreated,lastUpdated,metaClass')
@EqualsAndHashCode
class Example {

    public String nombre

    public Object valor

    public String clase

    public Example(String nombre,Object valor,String clase)
    {
        this.nombre=nombre
        this.clase=clase
        this.valor= (Class.forName(clase))valor;
    }


    static constraints = {
    }
}
