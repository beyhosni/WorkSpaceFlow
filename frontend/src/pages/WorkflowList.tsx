import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { workflowApi } from '../api/client';
import { Workflow } from '../types';

export default function WorkflowList() {
    const [workflows, setWorkflows] = useState<Workflow[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadWorkflows();
    }, []);

    const loadWorkflows = async () => {
        try {
            const response = await workflowApi.getAllWorkflows();
            setWorkflows(response.data);
        } catch (error) {
            console.error('Error loading workflows:', error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center h-64">
                <div className="text-gray-400">Loading...</div>
            </div>
        );
    }

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div className="flex items-center justify-between mb-8">
                <h1 className="text-3xl font-bold text-white">Workflows</h1>
                <Link
                    to="/workflows/create"
                    className="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg font-medium transition-colors"
                >
                    Create Workflow
                </Link>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {workflows.map((workflow) => (
                    <div key={workflow.id} className="bg-slate-800 rounded-lg border border-slate-700 p-6 hover:border-primary-500 transition-colors">
                        <div className="flex items-start justify-between mb-4">
                            <h3 className="text-xl font-semibold text-white">{workflow.name}</h3>
                            {workflow.active && (
                                <span className="px-2 py-1 bg-green-500/20 text-green-400 text-xs rounded-full">Active</span>
                            )}
                        </div>
                        <p className="text-gray-400 text-sm mb-4">{workflow.description}</p>
                        <div className="text-gray-500 text-sm mb-4">
                            {workflow.steps.length} step{workflow.steps.length !== 1 ? 's' : ''}
                        </div>
                        <div className="flex space-x-2">
                            <Link
                                to={`/workflows/${workflow.id}`}
                                className="flex-1 text-center bg-slate-700 hover:bg-slate-600 text-white px-4 py-2 rounded-lg text-sm transition-colors"
                            >
                                View Details
                            </Link>
                            <Link
                                to={`/workflows/${workflow.id}/start`}
                                className="flex-1 text-center bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg text-sm transition-colors"
                            >
                                Start Instance
                            </Link>
                        </div>
                    </div>
                ))}
            </div>

            {workflows.length === 0 && (
                <div className="text-center py-12">
                    <div className="text-gray-400 mb-4">No workflows found</div>
                    <Link
                        to="/workflows/create"
                        className="inline-block bg-primary-600 hover:bg-primary-700 text-white px-6 py-3 rounded-lg font-medium transition-colors"
                    >
                        Create Your First Workflow
                    </Link>
                </div>
            )}
        </div>
    );
}
