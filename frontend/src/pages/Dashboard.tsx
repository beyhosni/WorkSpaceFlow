import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { workflowApi, taskApi } from '../api/client';
import { WorkflowInstance, Task } from '../types';

export default function Dashboard() {
    const [instances, setInstances] = useState<WorkflowInstance[]>([]);
    const [tasks, setTasks] = useState<Task[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const [instancesRes, tasksRes] = await Promise.all([
                workflowApi.getAllInstances(),
                taskApi.getAllTasks(),
            ]);
            setInstances(instancesRes.data);
            setTasks(tasksRes.data);
        } catch (error) {
            console.error('Error loading dashboard data:', error);
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

    const activeInstances = instances.filter(i => i.status === 'IN_PROGRESS' || i.status === 'STARTED');
    const pendingTasks = tasks.filter(t => t.status === 'CREATED' || t.status === 'ASSIGNED');

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <h1 className="text-3xl font-bold text-white mb-8">Dashboard</h1>

            {/* Stats */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <div className="bg-slate-800 rounded-lg p-6 border border-slate-700">
                    <div className="text-gray-400 text-sm font-medium">Total Instances</div>
                    <div className="text-3xl font-bold text-white mt-2">{instances.length}</div>
                </div>
                <div className="bg-slate-800 rounded-lg p-6 border border-slate-700">
                    <div className="text-gray-400 text-sm font-medium">Active Instances</div>
                    <div className="text-3xl font-bold text-primary-400 mt-2">{activeInstances.length}</div>
                </div>
                <div className="bg-slate-800 rounded-lg p-6 border border-slate-700">
                    <div className="text-gray-400 text-sm font-medium">Pending Tasks</div>
                    <div className="text-3xl font-bold text-yellow-400 mt-2">{pendingTasks.length}</div>
                </div>
            </div>

            {/* Recent Instances */}
            <div className="bg-slate-800 rounded-lg border border-slate-700 mb-8">
                <div className="px-6 py-4 border-b border-slate-700 flex items-center justify-between">
                    <h2 className="text-xl font-semibold text-white">Recent Workflow Instances</h2>
                    <Link to="/instances" className="text-primary-400 hover:text-primary-300 text-sm">
                        View All
                    </Link>
                </div>
                <div className="p-6">
                    {instances.slice(0, 5).map((instance) => (
                        <div key={instance.id} className="flex items-center justify-between py-3 border-b border-slate-700 last:border-0">
                            <div>
                                <div className="text-white font-medium">{instance.workflowName}</div>
                                <div className="text-gray-400 text-sm">Started by {instance.startedBy}</div>
                            </div>
                            <div className="flex items-center space-x-4">
                                <span className={`px-3 py-1 rounded-full text-xs font-medium ${instance.status === 'COMPLETED' ? 'bg-green-500/20 text-green-400' :
                                        instance.status === 'FAILED' ? 'bg-red-500/20 text-red-400' :
                                            'bg-blue-500/20 text-blue-400'
                                    }`}>
                                    {instance.status}
                                </span>
                                <Link to={`/instances/${instance.id}`} className="text-primary-400 hover:text-primary-300">
                                    View
                                </Link>
                            </div>
                        </div>
                    ))}
                    {instances.length === 0 && (
                        <div className="text-center text-gray-400 py-8">No workflow instances yet</div>
                    )}
                </div>
            </div>

            {/* Pending Tasks */}
            <div className="bg-slate-800 rounded-lg border border-slate-700">
                <div className="px-6 py-4 border-b border-slate-700 flex items-center justify-between">
                    <h2 className="text-xl font-semibold text-white">Pending Tasks</h2>
                    <Link to="/tasks" className="text-primary-400 hover:text-primary-300 text-sm">
                        View All
                    </Link>
                </div>
                <div className="p-6">
                    {pendingTasks.slice(0, 5).map((task) => (
                        <div key={task.id} className="flex items-center justify-between py-3 border-b border-slate-700 last:border-0">
                            <div>
                                <div className="text-white font-medium">{task.name}</div>
                                <div className="text-gray-400 text-sm">{task.description}</div>
                            </div>
                            <div className="flex items-center space-x-4">
                                <span className={`px-3 py-1 rounded-full text-xs font-medium ${task.status === 'ASSIGNED' ? 'bg-yellow-500/20 text-yellow-400' :
                                        'bg-gray-500/20 text-gray-400'
                                    }`}>
                                    {task.status}
                                </span>
                                <Link to={`/tasks/${task.id}`} className="text-primary-400 hover:text-primary-300">
                                    View
                                </Link>
                            </div>
                        </div>
                    ))}
                    {pendingTasks.length === 0 && (
                        <div className="text-center text-gray-400 py-8">No pending tasks</div>
                    )}
                </div>
            </div>
        </div>
    );
}
