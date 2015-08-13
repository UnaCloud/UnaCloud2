package unacloud2

class UserController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of user services
	 */
	
	UserService userService
	
	//-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------
	
	/**
	 * Makes session verifications before executing user administration actions
	 */
	
	def beforeInterceptor = [action:{
			if(!session.user){
				flash.message="You must log in first"
				redirect(uri:"/login", absolute:true)
				return false
			}
			else if(!(session.user.userType.equals("Administrator"))){
				flash.message="You must be administrator to see this content"
				redirect(uri:"/error", absolute:true)
				return false
			}
		}, except: [
			'login',
			'logout',
			'home',
			'userHome',
			'account',
			'changePass',
			'refreshAPIKey'
		]]
	
	/**
	 * User index action
	 * @return List of all users
	 */
	def index() {
		[users: User.list(params)];
	}
	
	/**
	 * Home action. Selects redirection depending on session status and privileges
	 */
	
	def home(){
		if(!session.user){
			redirect(uri:"/login", absolute:true)
			return false
		}
		else if(!(session.user.userType.equals("Administrator")))
			redirect(uri:"/userHome", absolute:true)
		else{
			redirect(uri:"/adminHome", absolute:true)
		}
	}
	
	/**
	 * Redirects to administration users home
	 * @return
	 */
	
	def adminHome(){
		redirect(uri:"/mainpage", absolute:true)
	}
	
	/**
	 * Redirects to normal users home
	 * @return
	 */
	
	def userHome(){
		if(session.user)
			redirect(uri:"/functionalities", absolute:true)
		else{
			flash.message="You must log in first"
			redirect(uri:"/", absolute:true)
			return false
		}
	}
	
	/**
	 * Saves a new user. Redirects so user index when finished.
	 */
	
	def add() {
		userService.addUser(params.username, params.name+" "+params.lastname, params.userType,
				params.password )
		redirect(controller:"user" ,action:"index")
	}
	
	/**
	 * My Account form action
	 * @return session user for edition
	 */
	
	def account(){

		def u= User.get(session.user.id)
		if (!u) {
			redirect(action:"index")
		}
		else{
			[user: u]
		}
	}
	
	def downloadKey(){
		def u= User.get(session.user.id)
		if (!u) {
			redirect(action:"index")
		}
		else{
		def separator =  java.io.File.separatorChar
		def repository= Repository.findByName("Main Repository")
		File key= new File(repository.root+"keyPairs"+separator+"unacloud."+u.username+".pem")
		if(key.exists()){
			response.setContentType("application/octet-stream")
			response.setHeader("Content-disposition", "attachment;filename=${key.getName()}")
			
			response.outputStream << key.newInputStream()
		}
		}
	}
	/**
	 * Sets or changes an user restriction. Redirects to user index when finished.
	 */
	
	def setPolicy(){
		User u= User.findByUsername(params.username)
		userService.setPolicy(u, params.type,  params.value)
		redirect(action:"index")
	}
	
	/**
	 * Edit restrictions form action. Controls part of the interface render.
	 * @return User selected by user
	 */
	def editPerms(){
		def u= User.findByUsername(params.username)
		if (!u) {
			redirect(action:"index")
		}
		else{
			def found
			if(params.data!=null){
				for(UserRestriction allocPolicy:u.restrictions){
					if(allocPolicy.name.equals(params.data)){
						found=true
						render "<input id=\"value\" name=\"value\" type=\"text\" value=\""+allocPolicy.getValue()+"\">"
					}
				}
				if(found!=true){
				render "<input id=\"value\" name=\"value\" type=\"text\" value=\"\">"
				}
			}
			else{
			[user: u]
			}
		}
	}

	/**
	 * Changes user password. Redirects to my account page when finished.
	 */
	
	def changePass(){
		def u= User.get(session.user.id)
		if (!u) {
			redirect(action:"index")
		}
		else{
			if(u.password.equals(params.oldPassword)){
				if(params.newPassword.equals(params.confirmPassword)){
					userService.changePass(u, params.newPassword)
					redirect(uri:"/", absolute:true)
				}
				else {
					flash.message="Passwords don't match"
					redirect(uri:"/account", absolute:true)
				}
			}
			else{
				flash.message="Incorrect Password"
				redirect(uri:"/account", absolute:true)
			}
		}
	}
	
	/**
	 * Generates a new API key and saves it. Redirects to my account page when finished.
	 * @return
	 */
	def refreshAPIKey(){
		def u= User.get(session.user.id)
		if (!u) {
			redirect(action:"index")
		}
		else{
			userService.refreshAPIKey(u)
			redirect (action:"account")
		}
	}
	
	/**
	 * Deletes the selected user. Redirects to user index when finished.
	 */
	
	def delete(){
		def user = User.findByUsername(params.username)
		if (!user) {
			redirect(action:"list")
		}
		else{
			userService.deleteUser(user)
			redirect(controller:"user" ,action:"index")
		}
	}
	
	/**
	 * User edition form action.  
	 * @return selected user 
	 */
	
	def edit(){
		def u= User.findByUsername(params.username)

		if (!u) {
			redirect(action:"index")
		}
		else{
			[user: u]
		}
	}
	
	/**
	 * Saves user's information changes. Redirects to user index when finished. 
	 */
	
	def setValues(){
		def user = User.findByUsername(params.oldUsername)
		userService.setValues(user,params.username, params.name+" "+params.lastname, params.userType, params.password)
		redirect(action:"index")
	}
	
	/**
	 * Validates passwords and changes session user. Redirects to home page 
	 * if credentials are correct, or to login page if they're not  
	 */
	
	def login(){
		def user = User.findWhere(username:params.username,
		password:params.password)

		if (user){
			session.user = user
			flash.message=user.name
			redirect(uri: '/home', absolute: true)
		}
		else {
			flash.message="Wrong username or password"
			redirect(uri: '/login', absolute: true)
		}
	}
	
	/**
	 * Removes the current session user. Redirects to login page
	 * @return
	 */
	def logout(){
		session.invalidate()
		redirect(uri: '/', absolute: true)
	}
}