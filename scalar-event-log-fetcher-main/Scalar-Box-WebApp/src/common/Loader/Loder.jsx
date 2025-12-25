import React from "react";
import { SpinnerCircular } from "spinners-react";
import './loader.css'

function Loder() {
  return (
    <div className="loader">
      <SpinnerCircular
        size={71}
        thickness={135}
        speed={123}
        color="rgba(57, 66, 172, 1)"
        secondaryColor="rgba(0, 0, 0, 0.44)"
      />
    </div>
  );
}

export default Loder;
