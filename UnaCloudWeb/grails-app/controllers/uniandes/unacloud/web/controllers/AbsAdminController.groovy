package uniandes.unacloud.web.controllers

import uniandes.unacloud.web.domain.User;
import uniandes.unacloud.web.services.UserGroupService;

class AbsAdminController {

	/**
	 * Representation of group services
	 */
	
	UserGroupService userGroupService

    /**
	 * Makes session verifications before executing user administration actions
	 */	
	def beforeInterceptor = {
		if (!session.user) {
			flash.message = "You must log in first"
			redirect(uri:"/login", absolute:true)
			return false
		}
		else {
			def user = User.get(session.user.id)
			session.user.refresh(user)
			if (!userGroupService.isAdmin(user)) {
				flash.message = "You must be administrator to see this content"
				redirect(uri:"/error", absolute:true)
				return false
			}
		}
	}
}
