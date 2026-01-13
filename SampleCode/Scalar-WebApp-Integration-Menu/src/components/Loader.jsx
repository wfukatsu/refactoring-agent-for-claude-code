import { BeatLoader } from "react-spinners";

function Loader({ color = "#2834d8" }) {
  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        height: "100vh",
      }}
    >
      <BeatLoader color={color} />
    </div>
  );
}

export default Loader;
