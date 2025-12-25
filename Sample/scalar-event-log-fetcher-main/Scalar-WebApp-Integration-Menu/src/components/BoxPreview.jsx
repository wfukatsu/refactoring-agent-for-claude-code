/* eslint-disable react/prop-types */

import { useEffect, useState } from "react";
import { BASE_URL } from "../utils/constant";
import Loader from "./Loader";

const BoxPreview = ({ fileId }) => {
  const [boxToken, setBoxToken] = useState(null);

  const userName = "poojap@kanzencs.com";
  useEffect(() => {
    const fetchBoxToken = async () => {
      const headers = new Headers();
      headers.append("Content-Type", "application/json");
      const url = `${BASE_URL}/box/user/userSignIn/${userName}`;
      const response = await fetch(url, {
        method: "POST",
        headers: headers,
      });

      if (response.ok) {
        const jsonData = await response.json();
        const { accessToken } = { ...jsonData.data };
        setBoxToken(accessToken);
      } else {
        console.log("ELSE");
      }
    };

    fetchBoxToken();
  }, []);

  useEffect(() => {
    const script = document.createElement("script");
    script.src =
      "https://cdn01.boxcdn.net/platform/preview/2.54.0/en-US/preview.js";
    script.async = true;

    document.body.appendChild(script);

    return () => {
      document.body.removeChild(script);
    };
  }, []);

  useEffect(() => {
    if (
      window.Box &&
      typeof window.Box.Preview === "function" &&
      boxToken !== null
    ) {
      const preview = new window.Box.Preview();
      preview.show(fileId, boxToken, {
        container: ".preview-container",
        showDownload: true,
      });
    }
  }, [boxToken, fileId]);

  if (boxToken === null) {
    return <Loader />;
  }

  return (
    <div>
      {/* <div id="root"></div> */}
      <div
        className="preview-container"
        style={{ height: "500px", width: "600px" }}
      ></div>
    </div>
  );
};

export default BoxPreview;
