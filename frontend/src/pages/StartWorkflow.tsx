import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { workflowApi } from '../api/client';

export default function StartWorkflow() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [startedBy, setStartedBy] = useState('');
    const [variables, setVariables] = useState('{}');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        try {
            let parsedVariables = {};
            if (variables.trim()) {
                parsedVariables = JSON.parse(variables);
            }

            await workflowApi.startWorkflow({
                workflowId: id!,
                startedBy: startedBy || 'anonymous',
                variables: parsedVariables
            });

            navigate('/instances');
        } catch (error) {
            console.error('Error starting workflow:', error);
            alert('Failed to start workflow. Check the variables JSON format.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <h1 className="text-3xl font-bold text-white mb-8">Start Workflow Instance</h1>

            <form onSubmit={handleSubmit} className="bg-slate-800 rounded-lg border border-slate-700 p-6">
                <div className="mb-6">
                    <label className="block text-gray-300 text-sm font-medium mb-2">
                        Started By
                    </label>
                    <input
                        type="text"
                        value={startedBy}
                        onChange={(e) => setStartedBy(e.target.value)}
                        className="w-full bg-slate-700 border border-slate-600 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-primary-500"
                        placeholder="Your name"
                    />
                </div>

                <div className="mb-6">
                    <label className="block text-gray-300 text-sm font-medium mb-2">
                        Variables (JSON)
                    </label>
                    <textarea
                        value={variables}
                        onChange={(e) => setVariables(e.target.value)}
                        className="w-full bg-slate-700 border border-slate-600 rounded-lg px-4 py-2 text-white font-mono text-sm focus:outline-none focus:border-primary-500"
                        rows={6}
                        placeholder='{"key": "value"}'
                    />
                    <p className="text-gray-400 text-xs mt-2">Enter workflow variables as JSON</p>
                </div>

                <div className="flex space-x-4">
                    <button
                        type="submit"
                        disabled={loading}
                        className="bg-primary-600 hover:bg-primary-700 disabled:bg-slate-600 text-white px-6 py-2 rounded-lg font-medium transition-colors"
                    >
                        {loading ? 'Starting...' : 'Start Workflow'}
                    </button>
                    <button
                        type="button"
                        onClick={() => navigate('/workflows')}
                        className="bg-slate-700 hover:bg-slate-600 text-white px-6 py-2 rounded-lg font-medium transition-colors"
                    >
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    );
}
