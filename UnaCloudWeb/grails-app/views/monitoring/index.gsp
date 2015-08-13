<%@page import="unacloudEnums.MonitoringStatus"%>
<%@page import="unacloud2.PhysicalMachineStateEnum"%>

<html>
	<head>
		<meta name="layout" content="main"/>      
		<r:require modules="bootstrap"/>
	</head>
	<body>
		<div class="hero-unit span9">
	    <g:link controller="laboratory" action="getLab"  params="${[id: lab]}" style="display: -webkit-box;"><i class="icon-chevron-left" title="Back"></i><h5 style="margin: 3px;">Back to Physical Machine list</h5></g:link><br>
	   	<h4>${machine.name}</h4>	   	
	   	<div id="label-message"></div>
	   	<ul class="nav nav-tabs">
		  <li role="presentation" class="tab-header-monitor active" data-id = "metrics"><a href="#">CPU Metrics</a></li>
		  <li role="presentation" class="tab-header-monitor" data-id = "energy"><a href="#">Energy Report</a></li>
		  <li role="presentation" class="tab-header-monitor" data-id = "cpu"><a href="#">CPU Report</a></li>
		</ul>
		<div role="tabpanel" class="tab-pane active tab-monitor" id="metrics" style="background:white, ">
				<g:if test="${components==null}">
			   		<div class="alert alert-error" style="text-align:center"><i class="icon-exclamation-sign"></i><small> This machine does not have metrics in database</small></div>
			    </g:if>
			    <g:else>
					<table class="table table-bordered"  style="background:white" >
						<tr>
							<th>Component</th>
					  		<th>Value</th>
						</tr>	 
					    <tr>
					      	<td>	
					      		<small><strong>&nbsp;Operative System Name</strong></small>		        	
					        </td>
					        <td>
					   			<small>&nbsp;${components.operatingSystemName}</small> 
					   		</td>
						</tr>	
						<tr>
					      	<td>	
					      		<small><strong>&nbsp;Operative System Version</strong></small>		        	
					        </td>
					        <td>
					   			<small>&nbsp;${components.operatingSystemVersion}</small> 
					   		</td>
						</tr> 
						<tr>
					      	<td>	
					      		<small><strong>&nbsp;Operative System Arquitecture</strong></small>		        	
					        </td>
					        <td>
					   			<small>&nbsp;${components.operatingSystemArchitect}</small> 
					   		</td>
						</tr>
						<tr>
					      	<td>	
					      		<small><strong>&nbsp;CPU Model</strong></small>		        	
					        </td>
					        <td>
					   			<small>&nbsp;${components.cPUModel}</small> 
					   		</td>
						</tr>
						<tr>
					      	<td>	
					      		<small><strong>&nbsp;CPU Vendor</strong></small>		        	
					        </td>
					        <td>
					   			<small>&nbsp;${components.cPUVendor}</small> 
					   		</td>
						</tr>
						<tr>
					      	<td>	
					      		<small><strong>&nbsp;CPU Cores</strong></small>		        	
					        </td>
					        <td>
					   			<small>&nbsp;${components.cPUCores}</small> 
					   		</td>
						</tr>
						<tr>
					      	<td>	
					      		<small><strong>&nbsp;CPU Sockets</strong></small>		        	
					        </td>
					        <td>
					   			<small>&nbsp;${components.totalSockets}</small> 
					   		</td>
						</tr>
						<tr>
					      	<td>	
					      		<small><strong>&nbsp;CPU Mhz</strong></small>		        	
					        </td>
					        <td>
					   			<small>&nbsp;${components.cPUMhz}</small> 
					   		</td>
						</tr>
						<tr>
					      	<td>	
					      		<small><strong>&nbsp;Cores per Socket</strong></small>		        	
					        </td>
					        <td>
					   			<small>&nbsp;${components.coresPerSocket}</small> 
					   		</td>
						</tr>
						<tr>
					      	<td>	
					      		<small><strong>&nbsp;Ram Memory Size</strong></small>		        	
					        </td>
					        <td>
					   			<small>&nbsp;${components.rAMMemorySize}</small> 
					   		</td>
						</tr>
						<tr>
					      	<td>	
					      		<small><strong>&nbsp;Swap Memory Size</strong></small>		        	
					        </td>
					        <td>
					   			<small>&nbsp;${components.swapMemorySize}</small> 
					   		</td>
						</tr>
						<tr>
					      	<td>	
					      		<small><strong>&nbsp;Hard Disk Space</strong></small>		        	
					        </td>
					        <td>
					   			<small>&nbsp;${components.hardDiskSpace}</small> 
					   		</td>
						</tr>
						<tr>
					      	<td>	
					      		<small><strong>&nbsp;Hard Disk Filesystem</strong></small>		        	
					        </td>
					        <td>
					   			<small>&nbsp;${components.hardDiskSpace}</small> 
					   		</td>
						</tr>
						<tr>
					      	<td>	
					      		<small><strong>&nbsp;MAC</strong></small>		        	
					        </td>
					        <td>
					   			<small>&nbsp;${components.networkMACAddress}</small> 
					   		</td>
						</tr>
					</table>
				</g:else>	
		</div>
		<div role="tabpanel" class="tab-pane tab-monitor container span9" id="report" style="background:white ; padding: 20px">
			<form id="form_report" action="../getReports">
				<input type="hidden" name="host" value="${machine.name}">	
				<input id="input-report" type="hidden" name="report" value="">
				<div class=" form-group span6">
					<label for="year">Select Year:</label>
					<select name="year" class="form-control" id="year">
						<option>2015</option>
					</select>
					<label for="month">Select Month:</label>
					<select name="month" class="form-control" id="month">
					    <option>1</option>
					    <option>2</option>
					    <option>3</option>
					    <option>4</option>
					    <option>5</option>
					    <option>6</option>
					    <option>7</option>
					    <option>8</option>
					    <option>9</option>
					    <option>10</option>
					    <option>11</option>
					    <option>12</option>
					</select>
					<label for="day">Select Day:</label>
					<select name="day" class="form-control" id="day">
					    <option>1</option>
					    <option>2</option>
					    <option>3</option>
					    <option>4</option>
					    <option>5</option>
					    <option>6</option>
					    <option>7</option>
					    <option>8</option>
					    <option>9</option>
					    <option>10</option>
					    <option>11</option>
					    <option>12</option>
					    <option>13</option>
					    <option>14</option>
					    <option>15</option>
						<option>16</option>
						<option>17</option>
						<option>18</option>
						<option>19</option>
						<option>20</option>
						<option>21</option>
						<option>22</option>
						<option>23</option>
						<option>24</option>
						<option>25</option>
						<option>26</option>
						<option>27</option>
						<option>28</option>
						<option>29</option>
						<option>30</option>
						<option>31</option>
					</select>
					<label for="hour">Select the hour during the day:</label>
					<select name="hour" class="form-control" id="hour">
						<option>0</option>
						<option>1</option>
						<option>2</option>
						<option>3</option>
						<option>4</option>
						<option>5</option>
						<option>6</option>
						<option>7</option>
						<option>8</option>
						<option>9</option>
						<option>10</option>
						<option>11</option>
					</select>
					<label for="sched">AM or PM:</label>
					<select name="sched" class="form-control" id="sched">
						<option>am</option>
						<option>pm</option>
					</select>		
					<label for="range">Query for the next hours:</label>
					<select name="range" class="form-control" id="range">
						<option>1</option>
						<option>2</option>
						<option>3</option>
						<option>4</option>
						<option>5</option>
						<option>6</option>
					</select>		
					<button type="submit" class="btn btn-primary">Download</button>
				</div>			   
			</form>	
		</div>	   	
	</div>
	<g:javascript src="machine.js" />
	</body>
</html>