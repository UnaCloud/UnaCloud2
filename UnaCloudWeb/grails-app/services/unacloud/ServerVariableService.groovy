package unacloud

import grails.transaction.Transactional

@Transactional
class ServerVariableService {

    def getDefaultAllocator() {
		return ServerVariable.findByName("VM_DEFAULT_ALLOCATOR")
    }
}
