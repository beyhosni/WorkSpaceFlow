import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { taskApi } from '../api/client';
import { Task } from '../types';

export default function TaskList() {
    const [tasks, setTasks] = useState<Task[]>([]);
    const [loading, setLoading] = useState(true);
    const [assignee, setAssignee] = useState('');

    useEffect(() => {
        loadTasks();
    }, []);

    const loadTasks = async () => {
        try {
            const response = await taskApi.getAllTasks();
            setTasks(response.data);
        } catch (error) {
            console.error('Error loading tasks:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleAssign = async (taskId: string) => {
        if (!assignee.trim()) {
            alert('Please enter an assignee name');
            return;
        }

        try {
            await taskApi.assignTask(taskId, assignee);
            loadTasks();
            setAssignee('');
        } catch (error) {
            console.error('Error assigning task:', error);
            alert('Failed to assign task');
        }
    };

    const handleComplete = async (taskId: string) => {
        const completedBy = prompt('Enter your name:');
        if (!completedBy) return;

        try {
            await taskApi.completeTask(taskId, { completedBy });
            loadTasks();
        } catch (error) {
            console.error('Error completing task:', error);
            alert('Failed to complete task');
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
            <h1 className="text-3xl font-bold text-white mb-8">Tasks</h1>

            <div className="bg-slate-800 rounded-lg border border-slate-700">
                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-slate-700">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">
                                    Task Name
                                </th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">
                                    Status
                                </th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">
                                    Assignee
                                </th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">
                                    Created
                                </th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">
                                    Actions
                                </th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-700">
                            {tasks.map((task) => (
                                <tr key={task.id} className="hover:bg-slate-700/50">
                                    <td className="px-6 py-4">
                                        <div className="text-white font-medium">{task.name}</div>
                                        <div className="text-gray-400 text-sm">{task.description}</div>
                                    </td>
                                    <td className="px-6 py-4">
                                        <span className={`px-3 py-1 rounded-full text-xs font-medium ${task.status === 'COMPLETED' ? 'bg-green-500/20 text-green-400' :
                                                task.status === 'ASSIGNED' ? 'bg-yellow-500/20 text-yellow-400' :
                                                    'bg-gray-500/20 text-gray-400'
                                            }`}>
                                            {task.status}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 text-gray-300">
                                        {task.assignee || <span className="text-gray-500">Unassigned</span>}
                                    </td>
                                    <td className="px-6 py-4 text-gray-400 text-sm">
                                        {new Date(task.createdAt).toLocaleDateString()}
                                    </td>
                                    <td className="px-6 py-4">
                                        <div className="flex space-x-2">
                                            {task.status === 'CREATED' && (
                                                <div className="flex items-center space-x-2">
                                                    <input
                                                        type="text"
                                                        value={assignee}
                                                        onChange={(e) => setAssignee(e.target.value)}
                                                        placeholder="Assignee"
                                                        className="bg-slate-600 border border-slate-500 rounded px-2 py-1 text-white text-sm focus:outline-none focus:border-primary-500"
                                                    />
                                                    <button
                                                        onClick={() => handleAssign(task.id)}
                                                        className="bg-primary-600 hover:bg-primary-700 text-white px-3 py-1 rounded text-sm transition-colors"
                                                    >
                                                        Assign
                                                    </button>
                                                </div>
                                            )}
                                            {task.status === 'ASSIGNED' && (
                                                <button
                                                    onClick={() => handleComplete(task.id)}
                                                    className="bg-green-600 hover:bg-green-700 text-white px-3 py-1 rounded text-sm transition-colors"
                                                >
                                                    Complete
                                                </button>
                                            )}
                                            <Link
                                                to={`/tasks/${task.id}`}
                                                className="text-primary-400 hover:text-primary-300 text-sm"
                                            >
                                                View
                                            </Link>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

                {tasks.length === 0 && (
                    <div className="text-center text-gray-400 py-12">No tasks found</div>
                )}
            </div>
        </div>
    );
}
