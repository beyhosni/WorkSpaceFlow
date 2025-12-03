export interface StepDefinition {
    stepId: string;
    name: string;
    type: string;
    assigneeRole?: string;
    order: number;
}

export interface Workflow {
    id: string;
    name: string;
    description: string;
    steps: StepDefinition[];
    createdAt: string;
    updatedAt: string;
    createdBy: string;
    active: boolean;
}

export interface WorkflowInstance {
    id: string;
    workflowId: string;
    workflowName: string;
    status: string;
    currentStepId?: string;
    variables?: Record<string, any>;
    startedAt: string;
    completedAt?: string;
    startedBy: string;
}

export interface Task {
    id: string;
    workflowInstanceId: string;
    stepId: string;
    name: string;
    description: string;
    status: string;
    assignee?: string;
    assigneeRole?: string;
    data?: Record<string, any>;
    createdAt: string;
    assignedAt?: string;
    completedAt?: string;
    completedBy?: string;
}
