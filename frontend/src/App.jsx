import './App.css'
import * as React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import ToolpadRouter from './component/toolpad/ToolpadRouter'

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/*" element={<ToolpadRouter />} />
      </Routes>
    </Router>
  );
}

export default App
