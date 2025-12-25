import { Typography } from "@mui/material";
import BgImage from "../../assets/bg-image-audit-set-new.png";
// import BgImage from "../../assets/"
import "./ExternalAuditorCard.css";

export default function ExternalAuditorCard({
  id,
  count,
  title,
  selectedId,
  setSelected,
}) {
  const colour = selectedId === id ? "#fff" : "#000";

  return (
    <div className="card" onClick={() => setSelected(id)}>
      {selectedId === id && (
        <img src={BgImage} alt="Avatar" className="image" />
      )}
      <div
        className="text-card"
        style={{ paddingBottom: "15px", paddingTop: "15px" }}
      >
        {/* <h3>Assigned Audit Sets</h3> */}
        <Typography
          variant="h4"
          style={{ fontSize: "35px", fontWeight: "400", color: colour }}
        >
          {count}
        </Typography>
        <Typography
          variant="h8"
          style={{ fontSize: "18px", fontWeight: "400", color: colour }}
        >
          {title}
        </Typography>
      </div>
    </div>
  );
}
