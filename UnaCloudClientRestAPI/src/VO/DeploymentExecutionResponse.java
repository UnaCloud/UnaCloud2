package VO;

import java.util.List;

public class DeploymentExecutionResponse {
    private int deploymentId;

    private List<Integer> executions;

    public DeploymentExecutionResponse(int deploymentId, List<Integer> executions) {
        this.deploymentId = deploymentId;
        this.executions = executions;
    }

    public List<Integer> getExecutions() {
        return executions;
    }

    public void setExecutions(List<Integer> executions) {
        this.executions = executions;
    }

    public int getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(int deploymentId) {
        this.deploymentId = deploymentId;
    }
}
