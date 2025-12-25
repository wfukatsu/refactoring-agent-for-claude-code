import { useState } from "react";

export default function SCLable({ title, name }) {
  const [showText, setShowText] = useState(false);

  const handleMouseEnter = () => {
    if (name.length < 20) return;

    setShowText(true);
  };

  const handleMouseLeave = () => {
    if (name.length < 20) return;
    setShowText(false);
  };

  return (
    <>
      <p
        style={{ fontSize: "18px" }}
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
      >
        <b>{title} :</b>{" "}
        {showText
          ? name
          : name.length > 20
          ? name.substring(0, 20) + "..."
          : name}
      </p>
    </>
  );
}
