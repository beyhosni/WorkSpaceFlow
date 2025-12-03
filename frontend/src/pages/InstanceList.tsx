import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { workflowApi } from '../api/client';
import { WorkflowInstance } from '../types';

export default function InstanceList() {
    const [instances, setInstances] = useState<WorkflowInstance[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadInstances();
    }, []);

    const loadInstances = async () => {
        try {
            const response = await workflowApi.getAllInstances();
            setInstances(response.data);
        } catch (error) {
            console.error('Error loading instances:', error);
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
            <h1 className="text-3xl font-bold text-white mb-8">Workflow Instances</h1>

            <div className="bg-slate-800 rounded-lg border border-slate-700">
                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-slate-700">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">
                                    Workflow Name
                                </th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">
                                    Status
                                </th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">
                                    Started By
                                </th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">
                                    Started At
                                </th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">
                                    Actions
                                </th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-700">
                            {instances.map((instance) => (
                                <tr key={instance.id} className="hover:bg-slate-700/50">
                                    <td className="px-6 py-4 text-white font-medium">
                                        {instance.workflowName}
                                    </td>
                                    <td className="px-6 py-4">
                                        <span className={`px-3 py-1 rounded-full text-xs font-medium ${instance.status === 'COMPLETED' ? 'bg-green-500/20 text-green-400' :
                                                instance.status === 'FAILED' ? 'bg-red-500/20 text-red-400' :
                                                    'bg-blue-500/20 text-blue-400'
                                            }`}>
                                            {instance.status}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 text-gray-300">
                                        {instance.startedBy}
                                    </td>
                                    <td className="px-6 py-4 text-gray-400 text-sm">
                                        {new Date(instance.startedAt).toLocaleString()}
                                    </td>
                                    <td className="px-6 py-4">
                                        <Link
                                            to={`/instances/${instance.id}`}
                                            className="text-primary-400 hover:text-primary-300 text-sm"
                                        >
                                            View Details
                                        </Link>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

                {instances.length === 0 && (
                    <div className="text-center text-gray-400 py-12">No workflow instances found</div>
                )}
            </div>
        </div>
    );
}
