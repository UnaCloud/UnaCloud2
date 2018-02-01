package uniandes.unacloud.web.services


import grails.transaction.Transactional;
import uniandes.unacloud.web.domain.Example

@Transactional
class ExampleService {
	
	def getByAttribute(Long id)
	{
		return Example.get(id) 
	}
	
	def post(Example e)
	{
		e.save(failOnError:true)
	}
	
	def put (Long id,Example e)
	{
		if(Example.get(id))
		{
			e.id=id
			e.save(failOnError:true)
		}
		else System.out.println("ERROR");
	}
	
	def delete (Long id)
	{
		Example e=Example.getAt(id)
		if(e)
		{
			e.delete()
		}
		else System.out.println("ERROR");
	}
	
	static void main(String... args) {
		println 'Groovy world!'
	}
	
	

}
