package unacloud2

import unacloud2.PhysicalMachine;



class CheckMachineJob {
    static triggers = {
      cron name:'cronCheckMachines', startDelay:1000, cronExpression: '0 0/1 * 1/1 * ? *' //'0 0/2 * 1/1 * ? *' //
    }

    def execute() {
		try {
			Date current = new Date();
			long time = current.getTime()-60000*4;
			PhysicalMachine.executeUpdate('update PhysicalMachine pm set pm.state = \'OFF\', pm.withUser = 0 , pm.monitorStatus = \'DISABLE\', pm.monitorStatusEnergy = \'DISABLE\' where :date1 > pm.lastReport',[date1:new Date(time)]);
		} catch (Exception e) {
			e.printStackTrace()
		}
    }
}
