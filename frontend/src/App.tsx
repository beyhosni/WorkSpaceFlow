import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Dashboard from './pages/Dashboard';
import WorkflowList from './pages/WorkflowList';
import CreateWorkflow from './pages/CreateWorkflow';
import StartWorkflow from './pages/StartWorkflow';
import InstanceList from './pages/InstanceList';
import TaskList from './pages/TaskList';

function App() {
    return (
        <Router>
            <div className="min-h-screen bg-slate-900">
                <Navbar />
                <Routes>
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/workflows" element={<WorkflowList />} />
                    <Route path="/workflows/create" element={<CreateWorkflow />} />
                    <Route path="/workflows/:id/start" element={<StartWorkflow />} />
                    <Route path="/instances" element={<InstanceList />} />
                    <Route path="/tasks" element={<TaskList />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
