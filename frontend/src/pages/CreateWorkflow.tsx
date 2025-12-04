import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { workflowApi } from '../api/client';
import { StepDefinition } from '../types';

export default function CreateWorkflow() {
    const navigate = useNavigate();
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [steps, setSteps] = useState<StepDefinition[]>([
        { stepId: 'step1', name: '', type: 'HUMAN_TASK', assigneeRole: '', order: 1 }
    ]);
    const [loading, setLoading] = useState(false);

    const addStep = () => {
        setSteps([...steps, {
            stepId: `step${steps.length + 1}`,
            name: '',
            type: 'HUMAN_TASK',
            assigneeRole: '',
            order: steps.length + 1
        }]);
    };

    const removeStep = (index: number) => {
        setSteps(steps.filter((_, i) => i !== index));
    };

    const updateStep = (index: number, field: keyof StepDefinition, value: any) => {
        const newSteps = [...steps];
        newSteps[index] = { ...newSteps[index], [field]: value };
        setSteps(newSteps);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        try {
            await workflowApi.createWorkflow({
                name,
                description,
                steps: steps.map((step, index) => ({ ...step, order: index + 1 }))
            });
            navigate('/workflows');
        } catch (error) {
            console.error('Error creating workflow:', error);
            alert('Failed to create workflow');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <h1 className="text-3xl font-bold text-white mb-8">Create Workflow</h1>

            <form onSubmit={handleSubmit} className="bg-slate-800 rounded-lg border border-slate-700 p-6">
                <div className="mb-6">
                    <label className="block text-gray-300 text-sm font-medium mb-2">
                        Workflow Name *
                    </label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="w-full bg-slate-700 border border-slate-600 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-primary-500"
                        data-testid="workflow-name-input"
                        required
                    />
                </div>

                <div className="mb-6">
                    <label className="block text-gray-300 text-sm font-medium mb-2">
                        Description
                    </label>
                    <textarea
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        className="w-full bg-slate-700 border border-slate-600 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-primary-500"
                        data-testid="workflow-description-input"
                        rows={3}
                    />
                </div>

                <div className="mb-6">
                    <div className="flex items-center justify-between mb-4">
                        <label className="block text-gray-300 text-sm font-medium">
                            Workflow Steps *
                        </label>
                        <button
                            type="button"
                            onClick={addStep}
                            className="bg-primary-600 hover:bg-primary-700 text-white px-3 py-1 rounded text-sm transition-colors"
                        >
                            Add Step
                        </button>
                    </div>

                    {steps.map((step, index) => (
                        <div key={index} className="bg-slate-700 rounded-lg p-4 mb-4">
                            <div className="flex items-center justify-between mb-4">
                                <h3 className="text-white font-medium">Step {index + 1}</h3>
                                {steps.length > 1 && (
                                    <button
                                        type="button"
                                        onClick={() => removeStep(index)}
                                        className="text-red-400 hover:text-red-300 text-sm"
                                    >
                                        Remove
                                    </button>
                                )}
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-gray-400 text-sm mb-2">Step Name *</label>
                                    <input
                                        type="text"
                                        value={step.name}
                                        onChange={(e) => updateStep(index, 'name', e.target.value)}
                                        className="w-full bg-slate-600 border border-slate-500 rounded px-3 py-2 text-white focus:outline-none focus:border-primary-500"
                                        required
                                    />
                                </div>

                                <div>
                                    <label className="block text-gray-400 text-sm mb-2">Type *</label>
                                    <select
                                        value={step.type}
                                        onChange={(e) => updateStep(index, 'type', e.target.value)}
                                        className="w-full bg-slate-600 border border-slate-500 rounded px-3 py-2 text-white focus:outline-none focus:border-primary-500"
                                    >
                                        <option value="HUMAN_TASK">Human Task</option>
                                        <option value="AUTOMATED">Automated</option>
                                        <option value="APPROVAL">Approval</option>
                                    </select>
                                </div>

                                <div className="col-span-2">
                                    <label className="block text-gray-400 text-sm mb-2">Assignee Role</label>
                                    <input
                                        type="text"
                                        value={step.assigneeRole || ''}
                                        onChange={(e) => updateStep(index, 'assigneeRole', e.target.value)}
                                        className="w-full bg-slate-600 border border-slate-500 rounded px-3 py-2 text-white focus:outline-none focus:border-primary-500"
                                        placeholder="e.g., manager, developer"
                                    />
                                </div>
                            </div>
                        </div>
                    ))}
                </div>

                <div className="flex space-x-4">
                    <button
                        type="submit"
                        disabled={loading}
                        className="bg-primary-600 hover:bg-primary-700 disabled:bg-slate-600 text-white px-6 py-2 rounded-lg font-medium transition-colors"
                        data-testid="submit-workflow-button"
                    >
                        {loading ? 'Creating...' : 'Create Workflow'}
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
