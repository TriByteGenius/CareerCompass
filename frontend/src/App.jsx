import './App.css'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import Navbar from './component/Navbar'

function App() {
  return (
    <React.Fragment>
      <Router>
        <Navbar />
        <Routes>
          <Route path='/' element={ <Home />}/>
          {/* <Route path='/jobs' element={ <Job >}/>
          <Route path='/about' element={ <About />}/>
          <Route path='/contact' element={ <Contact />}/>
          <Route path='/login' element={ <Login >
          <Route path='/register' element={ <Register />}/> */}
        </Routes>
      </Router>
      <Toaster position='top-center'/>
    </React.Fragment>
  )
}

export default App
