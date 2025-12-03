import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Workflow API
export const workflowApi = {
    getAllWorkflows: () => apiClient.get('/workflows'),
    getWorkflowById: (id: string) => apiClient.get(`/workflows/${id}`),
    createWorkflow: (data: any) => apiClient.post('/workflows', data),
    startWorkflow: (data: any) => apiClient.post('/workflows/start', data),
    getAllInstances: () => apiClient.get('/workflows/instances'),
    getInstanceById: (id: string) => apiClient.get(`/workflows/instances/${id}`),
};

// Task API
export const taskApi = {
    getAllTasks: () => apiClient.get('/tasks'),
    getTaskById: (id: string) => apiClient.get(`/tasks/${id}`),
    getTasksByAssignee: (assignee: string) => apiClient.get(`/tasks/assignee/${assignee}`),
    assignTask: (id: string, assignee: string) => apiClient.put(`/tasks/${id}/assign?assignee=${assignee}`),
    completeTask: (id: string, data: any) => apiClient.put(`/tasks/${id}/complete`, data),
};

export default apiClient;
