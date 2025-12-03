import { Link } from 'react-router-dom';

export default function Navbar() {
    return (
        <nav className="bg-slate-800 border-b border-slate-700">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-between h-16">
                    <div className="flex items-center">
                        <Link to="/" className="flex items-center">
                            <span className="text-2xl font-bold bg-gradient-to-r from-primary-400 to-primary-600 bg-clip-text text-transparent">
                                WorkSpaceFlow
                            </span>
                        </Link>
                    </div>
                    <div className="flex space-x-4">
                        <Link
                            to="/"
                            className="text-gray-300 hover:bg-slate-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium transition-colors"
                        >
                            Dashboard
                        </Link>
                        <Link
                            to="/workflows"
                            className="text-gray-300 hover:bg-slate-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium transition-colors"
                        >
                            Workflows
                        </Link>
                        <Link
                            to="/instances"
                            className="text-gray-300 hover:bg-slate-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium transition-colors"
                        >
                            Instances
                        </Link>
                        <Link
                            to="/tasks"
                            className="text-gray-300 hover:bg-slate-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium transition-colors"
                        >
                            Tasks
                        </Link>
                    </div>
                </div>
            </div>
        </nav>
    );
}
