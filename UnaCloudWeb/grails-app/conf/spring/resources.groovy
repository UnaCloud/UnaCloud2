// Place your Spring DSL code here
beans = {
	multipartResolver(org.springframework.web.multipart.commons.CommonsMultipartResolver){
		
		// Max in memory 100kbytes
		maxInMemorySize=1024
		
		//20Gb Max upload size 21474836480
		maxUploadSize=9192000000
		//1Gb 9192000000
		//uploadTempDir="/tmp"
		
	}
}
